package com.aliyun.gts.financial.showcases.sofa.enums;

public enum OperationEnum {
    
    CREDIT("TransferIn"),

	DEBIT("TransferOut");

	private String code;

	OperationEnum(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}