package com.zff.xpanel.parser.view;

/**
 * <input j="30" s="20" x="147" y="387" w="101" h="29" pass="0" autoFocus="0" f="0" t="" l="0">
 */
public class EditTextArgs extends TextViewArgs{

	private String hint;
	private boolean isAutoFocus;
	private boolean isPass;
	private String f;
	
	public EditTextArgs(){
		setType(Type.EDITTEXT);
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public boolean isAutoFocus() {
		return isAutoFocus;
	}

	public void setAutoFocus(boolean isAutoFocus) {
		this.isAutoFocus = isAutoFocus;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public String getF() {
		return f;
	}

	public void setF(String f) {
		this.f = f;
	}
}
