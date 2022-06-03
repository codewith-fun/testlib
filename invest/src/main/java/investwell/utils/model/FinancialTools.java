package investwell.utils.model;

public class FinancialTools {
    private int imgFinancialTools;
    private String financialTerms;
    private String menuId;

    public FinancialTools(int imgFinancialTools, String financialTerms) {
        this.imgFinancialTools = imgFinancialTools;
        this.financialTerms = financialTerms;
    }

    public FinancialTools(int imgFinancialTools, String financialTerms, String menuId) {
        this.imgFinancialTools = imgFinancialTools;
        this.financialTerms = financialTerms;
        this.menuId = menuId;
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
