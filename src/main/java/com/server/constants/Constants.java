package com.server.constants;

public interface Constants {

    public static final String DEFAULT_USER_ID_ADMIN = "ADMIN";
    public static final String DEFAULT_MESSAGE_SUCCESSFUL = "SUCCESSFUL";
    public static final String DEFAULT_EMAIL = "email";
    public static final String DEFAULT_URL_AVATAR = "https://cdn-icons-png.flaticon.com/512/5847/5847555.png";
    public static final String DEFAULT_URL_LOGO_TEAM = "https://e7.pngegg.com/pngimages/619/729/png-clipart-the-international-2018-dota-2-vgj-storm-vgj-thunder-og-esports-logo-game-angle.png";


    public static final int DEFAULT_SIZE = 5;
    public static final int DEFAULT_PAGE = 0;

    public interface Regexp {
        public static final String REGEXP_PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d])[A-Za-z\\d\\W]{8,}$";
    }

}
