package com.tny.game.doc.controller;

import com.thoughtworks.xstream.annotations.*;
import com.tny.game.doc.*;
import com.tny.game.doc.holder.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@XStreamAlias("operation")
public class OperationConfiger {

	@XStreamAsAttribute
	private String methodName;

	@XStreamAsAttribute
	private int opId;

	@XStreamAsAttribute
	private String des;

	@XStreamAsAttribute
	private String text;

	@XStreamAsAttribute
	private String returnType;

	@XStreamAsAttribute
	private String returnDes;

	private ParamList paramList;

	@XStreamAlias("paramList")
	private static class ParamList {

		@XStreamAsAttribute
		@XStreamAlias("class")
		private String type = "list";

		@XStreamImplicit(itemFieldName = "param")
		private List<VarConfiger> paramList;

	}

	public OperationConfiger(FunDocHolder holder, TypeFormatter typeFormatter) {
		this.opId = holder.getOpId();
		this.returnType = holder.getFunDoc().returnType().getSimpleName();
		this.returnDes = holder.getFunDoc().returnDes();
		this.methodName = holder.getMethod().getName();
		this.paramList = new ParamList();
		this.des = holder.getFunDoc().des();
		this.text = holder.getFunDoc().text();
        if (StringUtils.isBlank(this.text)) {
            this.text = this.des;
        }
		List<VarConfiger> paramList = new ArrayList<VarConfiger>();
		for (VarDocHolder varDocHolder : holder.getParamList()) {
			paramList.add(new VarConfiger(varDocHolder, typeFormatter));
		}
		this.paramList.paramList = Collections.unmodifiableList(paramList);
	}

	public String getDes() {
		return des;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getReturnDes() {
		return returnDes;
	}

	public int getOpId() {
		return opId;
	}

	public String getMethodName() {
		return methodName;
	}

	public List<VarConfiger> getParamList() {
		return paramList.paramList;
	}

}
