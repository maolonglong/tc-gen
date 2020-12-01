package com.csl.tcg.property;

import com.csl.tcg.annotation.Prefix;
import com.csl.tcg.property.base.BaseProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author MaoLongLong
 * @date 2020-12-01 14:59
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
@Prefix("generator")
public class GeneratorProperty extends BaseProperty {

    private String outputDir;

    private String destDir;

    private Integer numberOfGroups;

    private Boolean enableZip;

    private String zipFileName;

    private String command;

}
