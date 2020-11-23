package com.aliyun.gts.financial.showcases.sofa.enums;

public enum StatusEnum {

	INIT("init"),

	DONE("done"),

	FAIL("fail");


	private String code;

	StatusEnum(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}