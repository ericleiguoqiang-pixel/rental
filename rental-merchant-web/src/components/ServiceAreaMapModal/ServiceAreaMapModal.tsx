import React, { useEffect, useRef, useState } from 'react';
import { Modal, Button, message } from 'antd';
import './ServiceAreaMapModal.css';

// 声明AMap全局变量
declare global {
  interface Window {
    AMap: any;
  }
}

// 防抖函数
const useDebounce = (callback: Function, delay: number) => {
  const timeoutRef = useRef<any>(null);
  
  return (...args: any[]) => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }
    
    timeoutRef.current = setTimeout(() => {
      callback(...args);
    }, delay);
  };
};

interface ServiceAreaMapModalProps {
  visible: boolean;
  onCancel: () => void;
  onFinish: (coordinates: any[]) => void;
  initialCoordinates: any[];
}

const ServiceAreaMapModal: React.FC<ServiceAreaMapModalProps> = ({
  visible,
  onCancel,
  onFinish,
  initialCoordinates
}) => {
  const mapContainerRef = useRef<HTMLDivElement>(null);
  const mapInstanceRef = useRef<any>(null);
  const polygonRef = useRef<any>(null);
  const mouseToolRef = useRef<any>(null);
  const [coordinates, setCoordinates] = useState<any[]>(initialCoordinates || []);
  const [isDrawing, setIsDrawing] = useState(false);
  
  // 保存当前的事件处理函数引用，用于清理
  const drawHandlerRef = useRef<any>(null);
  
  // 添加调试日志函数
  const addDebugLog = (msg: string) => {
    console.log(`[ServiceAreaMapModal] ${msg}`);
  };

  // 使用防抖更新坐标
  const debouncedSetCoordinates = useDebounce((coords: any[]) => {
    setCoordinates(coords);
  }, 100);

  // 组件卸载时清理资源
  const cleanupResources = () => {
    try {
      addDebugLog('开始清理资源');
      // 清理鼠标工具事件监听器
      if (mouseToolRef.current && drawHandlerRef.current) {
        try {
          addDebugLog('清理鼠标工具事件监听器');
          mouseToolRef.current.off('draw', drawHandlerRef.current);
        } catch (error) {
          addDebugLog(`清理鼠标工具事件监听器错误: ${error}`);
          console.error('清理鼠标工具事件监听器错误:', error);
        }
      }
      
      // 关闭鼠标工具
      if (mouseToolRef.current) {
        try {
          addDebugLog('关闭鼠标工具');
          mouseToolRef.current.close(true);
        } catch (error) {
          addDebugLog(`关闭鼠标工具错误: ${error}`);
          console.error('关闭鼠标工具错误:', error);
        }
      }
      
      // 清理多边形事件监听器
      if (polygonRef.current) {
        try {
          addDebugLog('清理多边形事件监听器');
          // 清理多边形的所有事件监听器
          polygonRef.current.off('adjust');
          polygonRef.current.off('dragstart');
        } catch (error) {
          addDebugLog(`清理多边形事件监听器错误: ${error}`);
          console.error('清理多边形事件监听器错误:', error);
        }
        
        // 移除多边形
        if (mapInstanceRef.current) {
          try {
            addDebugLog('移除多边形');
            mapInstanceRef.current.remove(polygonRef.current);
          } catch (error) {
            addDebugLog(`移除多边形错误: ${error}`);
            console.error('移除多边形错误:', error);
          }
        }
      }
      
      // 清理引用
      mouseToolRef.current = null;
      polygonRef.current = null;
      mapInstanceRef.current = null;
      drawHandlerRef.current = null;
      addDebugLog('资源清理完成');
    } catch (error) {
      addDebugLog(`组件卸载清理错误: ${error}`);
      console.error('组件卸载清理错误:', error);
    }
  };

  useEffect(() => {
    addDebugLog(`组件Effect触发, visible: ${visible}`);
    
    if (visible && mapContainerRef.current && !mapInstanceRef.current) {
      addDebugLog('初始化地图');
      // 初始化高德地图
      initMap();
    }
    
    if (visible && mapInstanceRef.current) {
      addDebugLog('更新坐标点');
      // 更新坐标点
      setCoordinates(initialCoordinates || []);
      setTimeout(() => {
        drawPolygon(initialCoordinates || []);
      }, 100);
    }
    
    // 组件卸载时清理资源
    return () => {
      addDebugLog('组件卸载，执行清理');
      cleanupResources();
    };
  }, [visible, initialCoordinates]);

  // 坐标数据清洗和验证函数
  const validateAndCleanCoordinates = (coords: any[]): any[] => {
    if (!coords || !Array.isArray(coords)) {
      return [];
    }
    
    return coords.filter(coord => {
      // 检查坐标是否为有效数组
      if (!coord || !Array.isArray(coord) || coord.length < 2) {
        return false;
      }
      
      // 检查经纬度是否有效
      const lng = typeof coord[0] === 'number' ? coord[0] : parseFloat(coord[0]);
      const lat = typeof coord[1] === 'number' ? coord[1] : parseFloat(coord[1]);
      
      // 检查转换后的值是否为有效数字
      if (isNaN(lng) || isNaN(lat)) {
        return false;
      }
      
      // 检查经纬度范围是否合理
      if (Math.abs(lng) > 180 || Math.abs(lat) > 90) {
        return false;
      }
      
      // 返回清洗后的坐标
      return [lng, lat];
    });
  };

  const initMap = () => {
    addDebugLog('开始初始化地图');
    
    if (!mapContainerRef.current) {
      addDebugLog('地图容器不存在');
      return;
    }
    
    // 检查是否已加载高德地图API
    if (typeof window.AMap === 'undefined') {
      addDebugLog('高德地图API未加载');
      message.error('地图加载失败，请检查网络连接');
      return;
    }

    try {
      addDebugLog('创建地图实例');
      // 创建地图实例
      const map = new window.AMap.Map(mapContainerRef.current, {
        zoom: 11,
        center: [116.397428, 39.90923], // 默认北京中心
      });
      
      mapInstanceRef.current = map;
      addDebugLog('地图实例创建成功');

      // 添加鼠标工具插件
      addDebugLog('加载鼠标工具插件');
      window.AMap.plugin(['AMap.MouseTool'], () => {
        try {
          addDebugLog('初始化鼠标工具');
          const mouseTool = new window.AMap.MouseTool(map);
          mouseToolRef.current = mouseTool;
          addDebugLog('鼠标工具初始化成功');
          
          // 定义绘制完成事件处理函数
          const drawHandler = (e: any) => {
            addDebugLog('绘制完成事件触发');
            // 立即移除事件监听器，防止重复触发
            if (mouseTool && drawHandlerRef.current) {
              addDebugLog('移除绘制事件监听器');
              mouseTool.off('draw', drawHandlerRef.current);
            }
            
            // 使用setTimeout确保事件处理在下一个事件循环中执行
            setTimeout(() => {
              try {
                addDebugLog('处理绘制完成事件');
                if (e.obj && e.obj.getPath) {
                  addDebugLog('获取路径坐标');
                  const path = e.obj.getPath();
                  const coords = path.map((point: any) => {
                    // 确保坐标数据格式正确
                    if (point && typeof point === 'object' && point.lng !== undefined && point.lat !== undefined) {
                      return [point.lng, point.lat];
                    }
                    return null;
                  }).filter(Boolean); // 过滤掉无效坐标
                  
                  addDebugLog(`获取到 ${coords.length} 个坐标点`);
                  // 清洗和验证坐标数据
                  const cleanedCoords = validateAndCleanCoordinates(coords);
                  // 直接设置坐标，避免防抖延迟导致的问题
                  setCoordinates(cleanedCoords);
                  addDebugLog('坐标状态更新完成');
                  // 清除绘制的图形，使用我们自己的polygon
                  if (mouseTool) {
                    addDebugLog('关闭鼠标工具');
                    mouseTool.close(true);
                  }
                  // 延迟绘制我们自己的多边形，确保状态已更新
                  setTimeout(() => {
                    addDebugLog('绘制自定义多边形');
                    drawPolygon(cleanedCoords);
                  }, 10);
                }
              } catch (error) {
                addDebugLog(`绘制完成事件处理错误: ${error}`);
                console.error('绘制完成事件处理错误:', error);
                message.error('绘制完成处理时出现错误');
              } finally {
                // 确保状态更新
                setTimeout(() => {
                  addDebugLog('设置绘制状态为完成');
                  setIsDrawing(false);
                }, 50);
              }
            }, 10);
          };
          
          // 保存事件处理函数引用，用于后续清理
          drawHandlerRef.current = drawHandler;
          
          // 添加事件监听器
          addDebugLog('添加绘制事件监听器');
          mouseTool.on('draw', drawHandler);
        } catch (error) {
          addDebugLog(`初始化鼠标工具错误: ${error}`);
          console.error('初始化鼠标工具错误:', error);
          message.error('地图工具初始化失败');
        }
      });

      // 如果有初始坐标，绘制多边形（也需要清洗数据）
      if (initialCoordinates && Array.isArray(initialCoordinates) && initialCoordinates.length > 0) {
        addDebugLog('绘制初始坐标');
        const cleanedInitialCoords = validateAndCleanCoordinates(initialCoordinates);
        setTimeout(() => {
          drawPolygon(cleanedInitialCoords);
        }, 100);
      }
    } catch (error) {
      addDebugLog(`初始化地图错误: ${error}`);
      console.error('初始化地图错误:', error);
      message.error('地图初始化失败');
    }
  };

  const drawPolygon = (coords: any[]) => {
    // 清洗坐标数据
    const cleanedCoords = validateAndCleanCoordinates(coords);
    
    if (!mapInstanceRef.current || cleanedCoords.length === 0) return;

    // 清除之前的多边形
    if (polygonRef.current) {
      try {
        addDebugLog('清理多边形事件监听器');
        // 清理多边形的所有事件监听器
        polygonRef.current.off('adjust');
        polygonRef.current.off('dragstart');
        mapInstanceRef.current.remove(polygonRef.current);
      } catch (error) {
        addDebugLog(`清理旧多边形错误: ${error}`);
        console.error('清理旧多边形错误:', error);
      }
    }

    try {
      addDebugLog('创建新的多边形');
      // 创建新的多边形
      const polygon = new window.AMap.Polygon({
        path: cleanedCoords.map(coord => new window.AMap.LngLat(coord[0], coord[1])),
        strokeColor: "#3366FF",
        strokeWeight: 3,
        strokeOpacity: 0.8,
        fillOpacity: 0.3,
        fillColor: '#1791fc',
        zIndex: 50,
        // 启用编辑功能
        editable: true,
      });

      // 定义多边形调整事件处理函数
      const adjustHandler = () => {
        try {
          const path = polygon.getPath();
          const coords = path.map((point: any) => {
            if (point && typeof point === 'object' && point.lng !== undefined && point.lat !== undefined) {
              return [point.lng, point.lat];
            }
            return null;
          }).filter(Boolean); // 过滤掉无效坐标
          
          // 清洗坐标数据
          const cleanedCoords = validateAndCleanCoordinates(coords);
          // 直接设置坐标
          setCoordinates(cleanedCoords);
        } catch (error) {
          addDebugLog(`多边形调整事件错误: ${error}`);
          console.error('多边形调整事件错误:', error);
        }
      };

      // 定义拖拽开始事件处理函数
      const dragStartHandler = () => {
        try {
          polygon.setOptions({ fillColor: '#1791fc' });
        } catch (error) {
          addDebugLog(`多边形拖拽开始事件错误: ${error}`);
          console.error('多边形拖拽开始事件错误:', error);
        }
      };

      // 添加事件监听器
      polygon.on('adjust', adjustHandler);
      polygon.on('dragstart', dragStartHandler);

      mapInstanceRef.current.add(polygon);
      polygonRef.current = polygon;

      // 调整地图视野以适应多边形
      mapInstanceRef.current.setFitView([polygon]);
      addDebugLog('多边形创建完成');
    } catch (error) {
      addDebugLog(`创建多边形错误: ${error}`);
      console.error('创建多边形错误:', error);
      message.error('绘制区域时出现错误，请重试');
    }
  };

  const startDrawing = () => {
    if (!mouseToolRef.current) {
      message.warning('地图工具尚未初始化完成，请稍后重试');
      return;
    }
    
    try {
      // 清除现有图形
      handleClear();
      setIsDrawing(true);
      
      // 开始绘制多边形
      mouseToolRef.current.polygon({
        strokeColor: "#3366FF",
        strokeWeight: 3,
        strokeOpacity: 0.8,
        fillOpacity: 0.3,
        fillColor: '#1791fc',
      });
    } catch (error) {
      console.error('开始绘制错误:', error);
      message.error('开始绘制时出现错误');
      setIsDrawing(false);
    }
  };

  const handleFinish = () => {
    if (coordinates.length < 3) {
      message.warning('请至少选择3个点来绘制区域');
      return;
    }
    try {
      onFinish(coordinates);
    } catch (error) {
      console.error('完成绘制错误:', error);
      message.error('保存区域时出现错误');
    }
  };

  const handleClear = () => {
    try {
      setCoordinates([]);
      if (polygonRef.current) {
        try {
          mapInstanceRef.current?.remove(polygonRef.current);
        } catch (error) {
          console.error('移除多边形错误:', error);
        }
        polygonRef.current = null;
      }
      if (mouseToolRef.current) {
        try {
          mouseToolRef.current.close(true);
        } catch (error) {
          console.error('关闭鼠标工具错误:', error);
        }
      }
    } catch (error) {
      console.error('清除操作错误:', error);
      message.error('清除操作时出现错误');
    }
  };

  return (
    <Modal
      title="设置服务范围"
      visible={visible}
      onCancel={onCancel}
      onOk={handleFinish}
      width={1000}
      footer={[
        <Button key="back" onClick={onCancel}>
          取消
        </Button>,
        <Button key="clear" onClick={handleClear}>
          清除
        </Button>,
        <Button 
          key="draw" 
          type="primary" 
          onClick={startDrawing}
          loading={isDrawing}
        >
          {isDrawing ? '绘制中...' : '开始绘制'}
        </Button>,
        <Button key="submit" type="primary" onClick={handleFinish}>
          确定
        </Button>,
      ]}
    >
      <div className="map-instructions">
        <p>点击"开始绘制"按钮，在地图上绘制服务范围区域，至少需要3个点。绘制完成后可拖拽顶点调整区域。</p>
      </div>
      <div 
        ref={mapContainerRef} 
        style={{ width: '100%', height: '500px' }}
        className="service-area-map"
      />
      <div className="coordinates-info">
        <p>已选择 {coordinates.length} 个坐标点</p>
        {coordinates.length > 0 && (
          <div className="coordinates-list">
            {coordinates.map((coord, index) => {
              // 添加安全检查，确保坐标数据有效
              if (!coord || !Array.isArray(coord) || coord.length < 2 || 
                  coord[0] === undefined || coord[1] === undefined) {
                return (
                  <span key={index} className="coordinate-item">
                    (无效坐标)
                  </span>
                );
              }
              
              // 确保坐标值是数字类型
              const lng = typeof coord[0] === 'number' ? coord[0] : parseFloat(coord[0]);
              const lat = typeof coord[1] === 'number' ? coord[1] : parseFloat(coord[1]);
              
              // 检查转换后的值是否有效
              if (isNaN(lng) || isNaN(lat)) {
                return (
                  <span key={index} className="coordinate-item">
                    (无效坐标)
                  </span>
                );
              }
              
              return (
                <span key={index} className="coordinate-item">
                  ({lng.toFixed(6)}, {lat.toFixed(6)})
                </span>
              );
            })}
          </div>
        )}
      </div>
    </Modal>
  );
};

export default ServiceAreaMapModal;