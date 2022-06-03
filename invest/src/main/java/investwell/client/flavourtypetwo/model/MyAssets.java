package investwell.client.flavourtypetwo.model;

public class MyAssets {
    private String tvMyAssetAmount, tvMyAssetType;
    private int ivMyAssetIcons;

    public MyAssets(String tvMyAssetAmount, String tvMyAssetType, int ivMyAssetIcons) {
        this.tvMyAssetAmount = tvMyAssetAmount;
        this.tvMyAssetType = tvMyAssetType;
        this.ivMyAssetIcons = ivMyAssetIcons;
    }

    public String getTvMyAssetAmount() {
        return tvMyAssetAmount;
    }

    public void setTvMyAssetAmount(String tvMyAssetAmount) {
        this.tvMyAssetAmount = tvMyAssetAmount;
    }

    public String getTvMyAssetType() {
        return tvMyAssetType;
    }

    public void setTvMyAssetType(String tvMyAssetType) {
        this.tvMyAssetType = tvMyAssetType;
    }

    public int getIvMyAssetIcons() {
        return ivMyAssetIcons;
    }

    public void setIvMyAssetIcons(int ivMyAssetIcons) {
        this.ivMyAssetIcons = ivMyAssetIcons;
    }
}
