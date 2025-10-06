package com.rental.saas.pricing.service.impl;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.ServiceAreaResponse;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.api.product.ProductClient;
import com.rental.api.product.response.CarModelProductResponse;
import com.rental.api.product.response.ValueAddedServiceTemplateResponse;
import com.rental.api.product.response.CancellationRuleTemplateResponse;
import com.rental.api.product.response.ServicePolicyTemplateResponse;
import com.rental.saas.common.enums.PickupType;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.pricing.dto.QuoteRequest;
import com.rental.saas.pricing.dto.QuoteResponse;
import com.rental.api.pricing.dto.QuoteDetailResponse;
import com.rental.api.pricing.entity.Quote;
import com.rental.saas.pricing.service.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 定价服务实现类
 */
@Slf4j
@Service
public class PricingServiceImpl implements PricingService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final BaseDataClient baseDataClient;
    private final ProductClient productClient;
    
    public PricingServiceImpl(RedisTemplate<String, Object> redisTemplate, 
                              BaseDataClient baseDataClient,
                              ProductClient productClient) {
        this.redisTemplate = redisTemplate;
        this.baseDataClient = baseDataClient;
        this.productClient = productClient;
    }
    
    /**
     * 根据位置和时间搜索报价
     * @param request 报价请求参数
     * @return 报价响应
     */
    @Override
    public QuoteResponse searchQuotes(QuoteRequest request) {
        log.info("搜索报价: date={}, time={}, longitude={}, latitude={}", 
                request.getDate(), request.getTime(), request.getLongitude(), request.getLatitude());
        
        // 1. 位置匹配：按照用户的位置，匹配服务区域取还车都覆盖了这个位置的门店，同时搜索出距离用户位置5公里的门店
        List<StoreResponse> matchedStores = matchStoresByLocation(request.getLongitude(), request.getLatitude());
        
        // 2. 过滤门店：针对门店设置的营业时间、提前预定时间和最大预定天数过滤出有效的门店
        List<StoreResponse> validStores = filterValidStores(matchedStores, request.getDate(), request.getTime());
        
        // 3. 过滤后的有效门店查询出门店的商品
        List<Quote> quotes = calculateQuotes(validStores, request);
        
        // 4. 保存报价到Redis缓存
        for (Quote quote : quotes) {
            saveQuoteToCache(quote);
        }
        
        QuoteResponse response = new QuoteResponse();
        response.setQuotes(quotes);
        
        return response;
    }
    
    /**
     * 获取报价详情
     * @param quoteId 报价ID
     * @return 报价详情响应
     */
    @Override
    public QuoteDetailResponse getQuoteDetail(String quoteId) {
        log.info("获取报价详情: quoteId={}", quoteId);
        
        // 从Redis缓存获取报价
        Quote quote = getQuoteFromCache(quoteId);
        if (quote == null) {
            return null;
        }
        
        QuoteDetailResponse response = new QuoteDetailResponse();
        response.setQuote(quote);
        
        // 获取增值服务模板列表
        List<ValueAddedServiceTemplateResponse> vasTemplates = getValueAddedServiceTemplates(quote);
        response.setVasTemplates(vasTemplates);
        
        // 获取取消规则
        CancellationRuleTemplateResponse cancellationPolicy = getCancellationRule(quote);
        response.setCancellationPolicy(cancellationPolicy);
        
        // 获取服务政策
        ServicePolicyTemplateResponse servicePolicy = getServicePolicy(quote);
        response.setServicePolicy(servicePolicy);
        
        return response;
    }
    
    /**
     * 获取增值服务模板列表
     * @param quote 报价信息
     * @return 增值服务模板列表
     */
    private List<ValueAddedServiceTemplateResponse> getValueAddedServiceTemplates(Quote quote) {
        List<ValueAddedServiceTemplateResponse> result = new ArrayList<>();
        
        try {
            // 获取商品信息
            ApiResponse<CarModelProductResponse> productResponse = productClient.getProductByStoreAndModel(
                quote.getStoreId(), quote.getModelId());
            
            if (productResponse != null && productResponse.getData() != null) {
                CarModelProductResponse product = productResponse.getData();
                
                // 获取所有增值服务模板
                ApiResponse<List<ValueAddedServiceTemplateResponse>> templatesResponse = 
                    productClient.getAllValueAddedServiceTemplates();
                
                if (templatesResponse != null && templatesResponse.getData() != null) {
                    List<ValueAddedServiceTemplateResponse> templates = templatesResponse.getData();
                    
                    // 根据商品关联的模板ID过滤
                    for (ValueAddedServiceTemplateResponse template : templates) {
                        // 如果商品关联了特定模板ID，则只返回关联的模板
                        if ((product.getVasTemplateId() != null && product.getVasTemplateId().equals(template.getId())) ||
                            (product.getVasTemplateIdVip() != null && product.getVasTemplateIdVip().equals(template.getId())) ||
                            (product.getVasTemplateIdVvip() != null && product.getVasTemplateIdVvip().equals(template.getId()))) {

                            ValueAddedServiceTemplateResponse dto = getValueAddedServiceTemplateResponse(template);

                            result.add(dto);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取增值服务模板列表失败", e);
        }
        
        return result;
    }

    @NotNull
    private static ValueAddedServiceTemplateResponse getValueAddedServiceTemplateResponse(ValueAddedServiceTemplateResponse template) {
        ValueAddedServiceTemplateResponse dto = new ValueAddedServiceTemplateResponse();
        dto.setId(template.getId());
        dto.setTemplateName(template.getTemplateName());
        dto.setServiceType(template.getServiceType());
        dto.setPrice(template.getPrice() / 100); // 转换为元
        dto.setDeductible(template.getDeductible());
        dto.setIncludeTireDamage(template.getIncludeTireDamage());
        dto.setIncludeGlassDamage(template.getIncludeGlassDamage());
        dto.setThirdPartyCoverage(template.getThirdPartyCoverage());
        dto.setChargeDepreciation(template.getChargeDepreciation());
        dto.setDepreciationDeductible(template.getDepreciationDeductible());
        dto.setDepreciationRate(template.getDepreciationRate());
        return dto;
    }

    /**
     * 获取取消规则
     * @param quote 报价信息
     * @return 取消规则
     */
    private CancellationRuleTemplateResponse getCancellationRule(Quote quote) {
        try {
            // 获取商品信息
            ApiResponse<CarModelProductResponse> productResponse = productClient.getProductByStoreAndModel(
                quote.getStoreId(), quote.getModelId());
            
            if (productResponse != null && productResponse.getData() != null) {
                CarModelProductResponse product = productResponse.getData();
                
                // 如果商品有关联的取消规则模板ID
                if (product.getCancellationTemplateId() != null) {
                    ApiResponse<CancellationRuleTemplateResponse> templateResponse = 
                        productClient.getCancellationRuleTemplateById(product.getCancellationTemplateId());
                    
                    if (templateResponse != null && templateResponse.getData() != null) {
                        CancellationRuleTemplateResponse template = templateResponse.getData();
                        
                        CancellationRuleTemplateResponse dto = new CancellationRuleTemplateResponse();
                        dto.setId(template.getId());
                        dto.setTemplateName(template.getTemplateName());
                        dto.setWeekdayRule(template.getWeekdayRule());
                        dto.setHolidayRule(template.getHolidayRule());
                        
                        return dto;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取取消规则失败", e);
        }
        
        return null;
    }
    
    /**
     * 获取服务政策
     * @param quote 报价信息
     * @return 服务政策
     */
    private ServicePolicyTemplateResponse getServicePolicy(Quote quote) {
        try {
            // 获取商品信息
            ApiResponse<CarModelProductResponse> productResponse = productClient.getProductByStoreAndModel(
                quote.getStoreId(), quote.getModelId());
            
            if (productResponse != null && productResponse.getData() != null) {
                CarModelProductResponse product = productResponse.getData();
                
                // 如果商品有关联的服务政策模板ID
                if (product.getServicePolicyTemplateId() != null) {
                    ApiResponse<ServicePolicyTemplateResponse> templateResponse = 
                        productClient.getServicePolicyTemplateById(product.getServicePolicyTemplateId());
                    
                    if (templateResponse != null && templateResponse.getData() != null) {
                        ServicePolicyTemplateResponse template = templateResponse.getData();
                        
                        ServicePolicyTemplateResponse dto = new ServicePolicyTemplateResponse();
                        dto.setId(template.getId());
                        dto.setTemplateName(template.getTemplateName());
                        dto.setMileageLimit(template.getMileageLimit());
                        dto.setEarlyPickup(template.getEarlyPickup());
                        dto.setLatePickup(template.getLatePickup());
                        dto.setEarlyReturn(template.getEarlyReturn());
                        dto.setRenewal(template.getRenewal());
                        dto.setForcedRenewal(template.getForcedRenewal());
                        dto.setPickupMaterials(template.getPickupMaterials());
                        dto.setCityRestriction(template.getCityRestriction());
                        dto.setUsageAreaLimit(template.getUsageAreaLimit());
                        dto.setFuelFee(template.getFuelFee());
                        dto.setPersonalBelongingsLoss(template.getPersonalBelongingsLoss());
                        dto.setViolationHandling(template.getViolationHandling());
                        dto.setRoadsideAssistance(template.getRoadsideAssistance());
                        dto.setForcedRecovery(template.getForcedRecovery());
                        dto.setEtcFee(template.getEtcFee());
                        dto.setCleaningFee(template.getCleaningFee());
                        dto.setInvoiceInfo(template.getInvoiceInfo());
                        
                        return dto;
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取服务政策失败", e);
        }
        
        return null;
    }
    
    /**
     * 生成报价ID
     * @return 报价ID
     */
    @Override
    public String generateQuoteId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 保存报价到Redis缓存
     * @param quote 报价信息
     */
    @Override
    public void saveQuoteToCache(Quote quote) {
        String key = "quote:" + quote.getId();
        redisTemplate.opsForValue().set(key, quote, 30, TimeUnit.MINUTES); // 缓存30分钟
    }
    
    /**
     * 从Redis缓存获取报价
     * @param quoteId 报价ID
     * @return 报价信息
     */
    @Override
    public Quote getQuoteFromCache(String quoteId) {
        String key = "quote:" + quoteId;
        return (Quote) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 位置匹配：按照用户的位置，匹配服务区域取还车都覆盖了这个位置的门店，同时搜索出距离用户位置5公里的门店
     * @param longitude 经度
     * @param latitude 纬度
     * @return 匹配的门店列表
     */
    private List<StoreResponse> matchStoresByLocation(Double longitude, Double latitude) {
        List<StoreResponse> result = new ArrayList<>();
        
        try {
            // 获取附近5公里内的门店
            ApiResponse<List<StoreResponse>> nearbyStoresResponse = baseDataClient.getNearbyStores(
                    longitude, latitude, 5.0);
            
            if (nearbyStoresResponse != null && nearbyStoresResponse.getData() != null) {
                result.addAll(nearbyStoresResponse.getData());
            }
            
            // TODO: 实现服务区域匹配逻辑（需要根据坐标判断是否在电子围栏内）
            // 这里暂时简化处理，只返回附近5公里的门店
            
        } catch (Exception e) {
            log.error("获取附近门店列表失败", e);
        }
        
        return result;
    }
    
    /**
     * 过滤门店：针对门店设置的营业时间、提前预定时间和最大预定天数过滤出有效的门店
     * @param stores 门店列表
     * @param date 预定日期
     * @param time 预定时间
     * @return 有效门店列表
     */
    private List<StoreResponse> filterValidStores(List<StoreResponse> stores, LocalDate date, LocalTime time) {
        List<StoreResponse> validStores = new ArrayList<>();
        
        for (StoreResponse store : stores) {
            try {
                // 检查营业时间
                if (time.isBefore(store.getBusinessStartTime()) || time.isAfter(store.getBusinessEndTime())) {
                    continue;
                }
                
                // 检查提前预定时间
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime bookingDateTime = LocalDateTime.of(date, time);
                long hoursBetween = java.time.Duration.between(now, bookingDateTime).toHours();
                
                if (hoursBetween < store.getMinAdvanceHours()) {
                    continue;
                }
                
                // 检查最大预定天数
                long daysBetween = java.time.Duration.between(now, bookingDateTime).toDays();
                
                if (daysBetween > store.getMaxAdvanceDays()) {
                    continue;
                }
                
                validStores.add(store);
            } catch (Exception e) {
                log.error("过滤门店时发生错误, storeId={}", store.getId(), e);
            }
        }
        
        return validStores;
    }
    
    /**
     * 计算报价
     * @param stores 有效门店列表
     * @param request 报价请求
     * @return 报价列表
     */
    private List<Quote> calculateQuotes(List<StoreResponse> stores, QuoteRequest request) {
        List<Quote> quotes = new ArrayList<>();
        
        for (StoreResponse store : stores) {
            try {
                // 查询门店的商品
                ApiResponse<List<CarModelProductResponse>> productsResponse = 
                    productClient.getProductsByStore(store.getId());
                
                if (productsResponse == null || productsResponse.getData() == null) {
                    continue;
                }
                
                // 为每个商品计算报价
                for (CarModelProductResponse product : productsResponse.getData()) {
                    // 计算价格
                    Quote quote = calculateQuoteForProduct(store, product, request);
                    if (quote != null) {
                        quotes.add(quote);
                    }
                }
            } catch (Exception e) {
                log.error("计算门店商品报价时发生错误, storeId={}", store.getId(), e);
            }
        }
        
        return quotes;
    }
    
    /**
     * 为商品计算报价
     * @param store 门店
     * @param product 商品
     * @param request 报价请求
     * @return 报价
     */
    private Quote calculateQuoteForProduct(StoreResponse store, CarModelProductResponse product, QuoteRequest request) {
        Quote quote = new Quote();
        quote.setId(generateQuoteId());
        quote.setProductId(product.getId());
        quote.setTenantId(product.getTenantId());
        quote.setProductName(product.getProductName());
        quote.setModelId(product.getCarModelId());
        quote.setStoreId(store.getId());
        quote.setStoreName(store.getStoreName());
        //quote.setCreatedAt(LocalDateTime.now());
        
        try {
            // 判断是否为周末来确定价格
            DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
            
            // 日租金
            BigDecimal dailyRate = new BigDecimal(isWeekend ? 
                product.getWeekendPrice() : product.getWeekdayPrice())
                .divide(new BigDecimal(100)); // 转换为元
            
            quote.setDailyRate(dailyRate);
            
            // 门店手续费（从门店信息中获取，转换为元）
            BigDecimal storeFee = new BigDecimal(store.getServiceFee())
                .divide(new BigDecimal(100));
            quote.setStoreFee(storeFee);
            
            // 基本保障价格（这里简化处理，实际应该从商品配置中获取）
            BigDecimal baseProtectionPrice = new BigDecimal("30.00");
            quote.setBaseProtectionPrice(baseProtectionPrice);
            
            // 获取服务范围信息来确定取还方式和费用
            ApiResponse<List<ServiceAreaResponse>> serviceAreasResponse = 
                baseDataClient.getServiceAreasByStore(store.getId());
            
            BigDecimal pickupFee = BigDecimal.ZERO;
            BigDecimal returnFee = BigDecimal.ZERO;
            PickupType deliveryType = PickupType.PICKUP_TYPE_STORE; // 默认为到店自取
            
            if (serviceAreasResponse != null && serviceAreasResponse.getData() != null) {
                List<ServiceAreaResponse> serviceAreas = serviceAreasResponse.getData();
                
                // 查找取车区域
                ServiceAreaResponse pickupArea = serviceAreas.stream()
                    .filter(area -> area.getAreaType() == 1 && area.getDoorToDoorDelivery() == 1)
                    .findFirst()
                    .orElse(null);
                
                // 查找还车区域
                ServiceAreaResponse returnArea = serviceAreas.stream()
                    .filter(area -> area.getAreaType() == 2 && area.getDoorToDoorDelivery() == 1)
                    .findFirst()
                    .orElse(null);
                
                // 如果找到了上门服务区域，则设置为上门取送车
                if (pickupArea != null && returnArea != null) {
                    deliveryType = PickupType.PICKUP_TYPE_SEND;
                    pickupFee = new BigDecimal(pickupArea.getDeliveryFee())
                        .divide(new BigDecimal(100));
                    returnFee = new BigDecimal(returnArea.getDeliveryFee())
                        .divide(new BigDecimal(100));
                }
            }
            
            quote.setPickupFee(pickupFee);
            quote.setReturnFee(returnFee);
            quote.setDeliveryType(deliveryType.getDescription());
            
            // 计算押金信息
            // 车损押金和违章押金从商品信息中获取，转换为元
            BigDecimal damageDeposit = new BigDecimal(product.getDamageDeposit())
                .divide(new BigDecimal(100));
            BigDecimal violationDeposit = new BigDecimal(product.getViolationDeposit())
                .divide(new BigDecimal(100));
            
            quote.setDamageDeposit(damageDeposit);
            quote.setViolationDeposit(violationDeposit);
            
            // 计算总价格（不包含押金，押金只是冻结金额）
            BigDecimal totalPrice = dailyRate
                .add(pickupFee)
                .add(returnFee)
                .add(storeFee)
                .add(baseProtectionPrice);
            
            quote.setTotalPrice(totalPrice);
            
        } catch (Exception e) {
            log.error("计算商品报价时发生错误, productId={}, storeId={}", product.getId(), store.getId(), e);
            return null;
        }
        
        return quote;
    }
}