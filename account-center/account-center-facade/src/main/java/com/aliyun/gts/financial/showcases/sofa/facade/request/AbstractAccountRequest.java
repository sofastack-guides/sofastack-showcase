package com.aliyun.gts.financial.showcases.sofa.facade.request;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AbstractAccountRequest implements Serializable {

    /** 序列号 */
    private static final long serialVersionUID = -5681817694181854590L;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}