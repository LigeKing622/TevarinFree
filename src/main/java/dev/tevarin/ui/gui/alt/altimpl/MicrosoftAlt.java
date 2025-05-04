package dev.tevarin.ui.gui.alt.altimpl;


import dev.tevarin.ui.gui.alt.AccountEnum;
import dev.tevarin.ui.gui.alt.Alt;

public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName, String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
