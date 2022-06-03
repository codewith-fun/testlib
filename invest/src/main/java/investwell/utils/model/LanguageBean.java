package investwell.utils.model;

public class LanguageBean {
    private String langName;
    private String langAlternateName;

    public LanguageBean(String langName, String langAlternateName) {
        this.langName = langName;
        this.langAlternateName = langAlternateName;
    }

    public String getLangName() {
        return langName;
    }



    public String getLangAlternateName() {
        return langAlternateName;
    }


}
