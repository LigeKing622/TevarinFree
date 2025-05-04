package dev.tevarin;


import dev.tevarin.command.CommandManager;
import dev.tevarin.config.ConfigManager;
import dev.tevarin.event.EventManager;

import dev.tevarin.manager.*;
import dev.tevarin.module.Module;
import dev.tevarin.module.ModuleManager;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.gui.main.GuiGoodBye;

import dev.tevarin.ui.hud.HUDManager;

import dev.tevarin.ui.sidegui.SideGUI;
import dev.tevarin.utils.*;
import dev.tevarin.utils.math.Killaura1;
import dev.tevarin.utils.player.BadPacketsComponent;
import dev.tevarin.utils.render.WallpaperEngine;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.netease.chunk.WorldLoader;
import net.vialoadingbase.ViaLoadingBase;
import net.viamcp.ViaMCP;
import org.lwjglx.opengl.Display;
import sun.misc.Unsafe;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Getter
@Setter
public class Client {
    public static String name = "Tevarin";
    public static String version = "1.0";
    public static final Boolean Verify = true;
    public static final String Verify_http = "https://gitee.com/yu-dagang-1337/tevarin-client-by-yu-dagang/raw/master/Hwid";
    public static Client instance;
    public static final String location = AutoDiYuQiShi.getLocation();
    public String user = "";
    public static String userName;
    private final SideGUI sideGui = new SideGUI();
    public EventManager eventManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public FriendManager friendManager;
    private PacketDelayHandler packetDelayHandler;
    private PacketBlinkHandler packetBlinkHandler;
    public ConfigManager configManager;
    public PacketManager packetManager;
    public SlotSpoofManager slotSpoofManager;
    public WallpaperEngine wallpaperEngine;
    public BlinkManager blinkManager;
    public PacketStoringComponent pcketStoringComponent;
    public BlinkHandler blinkHandler;
    public BadPacketUComponent badPacketUComponent;
    public BadPacketsComponent badPacketsComponent;
    public HUDManager hudManager;
    public RotationManager rotationManager;
    public StuckComponent stuckComponent;
    public MovementComponent movementComponent;
    public FallDistanceManager fallDistanceManager;
    public BanManager banManager;
    public String ingameName;
    public boolean canSendMotionPacket = true;
    public boolean clientLoadFinished = false;
    public static Minecraft mc;
    public static final Unsafe theUnsafe;
    public static boolean oOOoo = false;
    public static TrayIcon trayIcon;
    public static boolean isIntroFinish = false;

    public void init() {
        mc.drawSplashScreen(55);
        try {
            instance = this;
            if (Client.Verify){
                Killaura1.showMessageDialog();
            }

            System.out.println("Starting " + name + " " + version);
            mc.drawSplashScreen(60);
            this.eventManager = new EventManager();
            this.moduleManager = new ModuleManager();

            this.commandManager = new CommandManager();
            this.stuckComponent = new StuckComponent();
            this.configManager = new ConfigManager();
            this.packetDelayHandler = new PacketDelayHandler();
            this.packetBlinkHandler = new PacketBlinkHandler();
            this.blinkHandler = new BlinkHandler();
            this.hudManager = new HUDManager();
            this.rotationManager = new RotationManager();
            this.fallDistanceManager = new FallDistanceManager();
            this.pcketStoringComponent = new PacketStoringComponent();
            this.packetManager = new PacketManager();
            this.badPacketUComponent = new BadPacketUComponent();
            this.slotSpoofManager = new SlotSpoofManager();
            this.badPacketsComponent = new BadPacketsComponent();
            this.movementComponent = new MovementComponent();

            this.blinkManager = new BlinkManager();
            this.banManager = new BanManager();
            this.friendManager = new FriendManager();
            this.eventManager.register(this);

            this.eventManager.register(this.rotationManager);
            this.eventManager.register(this.stuckComponent);
            this.eventManager.register(this.movementComponent);
            this.eventManager.register(this.fallDistanceManager);
            this.eventManager.register(this.pcketStoringComponent);
            this.eventManager.register(this.blinkHandler);
            this.eventManager.register(this.banManager);
            this.eventManager.register(this.badPacketUComponent);
            this.eventManager.register(this.packetManager);
            //this.eventManager.register((Object)PacketProcessor.INSTANCE);
            this.eventManager.register(new WorldLoader());
            this.moduleManager.init();
            this.commandManager.init();
            this.configManager.loadAllConfig();
            if (oOOoo) {
                //Killaura1.b();
            }
            try {
                ViaMCP.create();
                ViaMCP.INSTANCE.initAsyncSlider();
                ViaLoadingBase.getInstance().reload(ProtocolVersion.v1_12_2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.wallpaperEngine = new WallpaperEngine();
            String gameDirPath = Client.mc.mcDataDir.getAbsolutePath();
            String videoFilePath = gameDirPath + File.separator + "background.mp4";
            try {
                InputStream inputStream = Client.class.getResourceAsStream("/assets/minecraft/tevarin/MainMenu/background.mp4");
                Files.copy(inputStream, Paths.get(videoFilePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/assets/minecraft/tevarin/ICON/LaunchIcon.png"));
            trayIcon = new TrayIcon(imageIcon.getImage());
            if (SystemTray.isSupported()) {
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("Tevarin " + version + " made by ??? with ❤~");
                PopupMenu popupMenu = new PopupMenu("Client settings");
                Menu moduleMenu = new Menu("Modules");
                for (Module module : moduleManager.getModuleMap().values()) {
                    final Module m = module;
                    final CheckboxMenuItem checkboxMenuItem = new CheckboxMenuItem(m.getName(), m.state);
                    checkboxMenuItem.addItemListener(e -> m.setState(!m.state));
                    //m.setCheckboxMenuItem(checkboxMenuItem);
                    moduleMenu.add(checkboxMenuItem);
                }
                popupMenu.add(moduleMenu);
                popupMenu.addSeparator();
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(e -> {
                    trayIcon.displayMessage(Client.name + " - Notification", "下次再见！", TrayIcon.MessageType.INFO);
                    SystemTray.getSystemTray().remove(trayIcon);
                    mc.displayGuiScreen(new GuiGoodBye());
                });

                popupMenu.add(exitItem);
                trayIcon.setPopupMenu(popupMenu);

                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException ignored) {
                }

                trayIcon.displayMessage(Client.name + version, "欢迎回来!", TrayIcon.MessageType.INFO);
            }
            this.wallpaperEngine.setup(new File(videoFilePath), 30);
            this.clientLoadFinished = true;
            this.user = Minecraft.getMinecraft().getSession().getUsername();
            Display.setTitle(name + " " + version + " - " + this.user);
        } catch (Exception e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
            theUnsafe.freeMemory(114514L);
        }
    }

    public void shutdown() {
        System.out.println("Client shutdown");


    }
    public PacketDelayHandler getPacketDelayHandler() {
        return this.packetDelayHandler;
    }
    public PacketBlinkHandler getPacketBlinkHandler() {
        return this.packetBlinkHandler;
    }

    public static int getClientColor(int Alpha) {
        java.awt.Color color = HUD.color(1);
        return color.getRGB();
    }
    public static String getIGN() {
        return Client.mc.thePlayer == null ? mc.getSession().getUsername() : Client.mc.thePlayer.getName();
    }

    public String getUser() {
        return this.user;
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public PacketManager getPacketManager() {
        return this.packetManager;
    }

    public SlotSpoofManager getSlotSpoofManager() {
        return this.slotSpoofManager;
    }

    public WallpaperEngine getWallpaperEngine() {
        return this.wallpaperEngine;
    }

    public BlinkManager getBlinkManager() {
        return this.blinkManager;
    }

    public HUDManager getHudManager() {
        return this.hudManager;
    }

    public RotationManager getRotationManager() {
        return this.rotationManager;
    }

    public FallDistanceManager getFallDistanceManager() {
        return this.fallDistanceManager;
    }

    public BanManager getBanManager() {
        return this.banManager;
    }

    public String getIngameName() {
        return this.ingameName;
    }

    public boolean isClientLoadFinished() {
        return this.clientLoadFinished;
    }
    public static int getBlueColor() {
        return new java.awt.Color(47, 154, 241).getRGB();
    }
    public static java.awt.Color getBlueColor(int alpha) {
        return new java.awt.Color(47, 154, 241,alpha);
    }

    public static int getBBlueColor() {
        return new java.awt.Color(0, 125, 255).getRGB();
    }

    static {
        mc = Minecraft.getMinecraft();
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
