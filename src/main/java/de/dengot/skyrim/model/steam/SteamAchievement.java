package de.dengot.skyrim.model.steam;

public class SteamAchievement {

    private String apiName;
    private String name;
    private String description;
    private String logo;

    public SteamAchievement(String apiName, String name, String description, String logo) {
        super();
        this.apiName = apiName;
        this.name = name;
        this.description = description;
        this.logo = logo;
    }

    public String getApiName() {
        return apiName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLogo() {
        return logo;
    }

}
