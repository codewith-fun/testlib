package investwell.utils.model;

import android.os.Bundle;

public class AddTrans {
    private int imgFinancialTools;
    private String financialTerms;
    private String menuId;
    private Bundle bundle;

    public AddTrans(int imgAddTrans, String financialTerms,Bundle bundle) {
        this.imgFinancialTools = imgAddTrans;
        this.financialTerms = financialTerms;
        this.bundle=bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public int getImgFinancialTools() {
        return imgFinancialTools;
    }

    public void setImgFinancialTools(int imgFinancialTools) {
        this.imgFinancialTools = imgFinancialTools;
    }

    public String getFinancialTerms() {
        return financialTerms;
    }

    public void setFinancialTerms(String financialTerms) {
        this.financialTerms = financialTerms;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
