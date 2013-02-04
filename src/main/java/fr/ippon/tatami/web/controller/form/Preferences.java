package fr.ippon.tatami.web.controller.form;

import fr.ippon.tatami.domain.User;
import org.apache.commons.lang.StringUtils;

/**
 * Stores a user's preferences.
 */
public class Preferences {

    private Boolean mentionEmail = false;

    String theme = "";

    private Boolean rssUidActive = false;

    private String rssUid;

    private String[] themes = {"bootstrap","cerulean","cosmo","journal","readable","simplex","spacelab",
            "spruce","superhero","united"};

    public Preferences() {

    }

    public Preferences(User user) {
        this.mentionEmail = user.getPreferencesMentionEmail();
        this.theme = user.getTheme();
        if (!StringUtils.isEmpty(user.getRssUid())) {
            this.rssUidActive = true;
            this.rssUid = user.getRssUid();
        }
    }

    public Boolean getMentionEmail() {
        return mentionEmail;
    }

    public void setMentionEmail(Boolean mentionEmail) {
        this.mentionEmail = mentionEmail;
    }

    public Boolean getRssUidActive() {
        return this.rssUidActive;
    }

    public void setRssUidActive(boolean rssUidActive) {
        this.rssUidActive = rssUidActive;
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getRssUid() {
        return rssUid;
    }

    public void setRssUid(String rssUid) {
        this.rssUid = rssUid;
    }

    public void setThemesList(String themes) {
        if (themes != null && !themes.isEmpty()) {
            this.themes = themes.split(",");
        }
    }

    public String[] getThemesList() {
        return themes;
    }



}
