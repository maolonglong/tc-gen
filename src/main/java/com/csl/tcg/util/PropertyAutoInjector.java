package com.csl.tcg.util;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.csl.tcg.annotation.Prefix;
import com.csl.tcg.property.base.BaseProperty;

import java.lang.reflect.Field;

/**
 * @author MaoLongLong
 * @date 2020-12-01 15:05
 */
public class PropertyAutoInjector {

    private PropertyAutoInjector() {
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static void inject(BaseProperty property, String path) {
        Props props = new Props(path);

        Class<? extends BaseProperty> clazz = property.getClass();
        Prefix prefixAnn = clazz.getAnnotation(Prefix.class);
        String prefix = prefixAnn.value();

        Field[] fields = ReflectUtil.getFields(clazz);
        for (Field field : fields) {
            String fieldName = field.getName();
            String key = StrUtil.join("", prefix, StrUtil.DOT, fieldName);
            ReflectUtil.setFieldValue(property, field, props.getObj(key));
        }
    }

}
