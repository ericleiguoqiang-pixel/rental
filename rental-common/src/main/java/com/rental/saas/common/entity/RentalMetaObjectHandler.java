package com.rental.saas.common.entity;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RentalMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        if (metaObject.hasSetter("createdBy")) {
            this.strictInsertFill(metaObject, "createdBy", Long.class, 1L);
        }
        if (metaObject.hasSetter("updatedBy")) {
            this.strictInsertFill(metaObject, "updatedBy", Long.class, 1L);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        if (metaObject.hasSetter("updatedBy")) {
            this.strictInsertFill(metaObject, "updatedBy", Long.class, 1L);
        }
    }
}
