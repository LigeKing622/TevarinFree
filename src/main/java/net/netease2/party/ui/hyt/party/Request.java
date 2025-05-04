/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.party.ui.hyt.party;

public class Request {
    private final String name;
    private final String acceptId;
    private final String denyId;

    public Request(String name, String acceptId, String denyId) {
        this.name = name;
        this.acceptId = acceptId;
        this.denyId = denyId;
    }

    public String getName() {
        return this.name;
    }

    public String getAcceptId() {
        return this.acceptId;
    }

    public String getDenyId() {
        return this.denyId;
    }
}

