package li.cil.oc;

import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Localization {
    private static String resolveKey(String key) {
        if (canLocalize(Settings.namespace + key))
            return Settings.namespace + key;
        else
            return key;
    }

    public static boolean canLocalize(String key) {
        return LanguageMap.getInstance().has(key);
    }

    public static TranslationTextComponent localizeLater(String formatKey, Object... values) {
        return new TranslationTextComponent(resolveKey(formatKey), values);
    }

    public static TranslationTextComponent localizeLater(String formatKey) {
        return new TranslationTextComponent(resolveKey(formatKey));
    }

    public static String localizeImmediately(String formatKey, Object... values) {
        String k = resolveKey(formatKey);
        LanguageMap lm = LanguageMap.getInstance();
        if (!lm.has(k)) return k;
        return Arrays.stream(String.format(lm.getOrDefault(k), values).split("\\r?\\n"))
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    public static String localizeImmediately(String formatKey) {
        String k = resolveKey(formatKey);
        LanguageMap lm = LanguageMap.getInstance();
        if (!lm.has(k)) return k;
        return Arrays.stream(lm.getOrDefault(k).split("\\r?\\n"))
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    public static class Analyzer {
        public static IFormattableTextComponent Address(String value) {
            IFormattableTextComponent result = localizeLater("gui.Analyzer.Address", value);
            return result.setStyle(result.getStyle()
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, localizeLater("gui.Analyzer.CopyToClipboard"))));
        }

        public static TranslationTextComponent AddressCopied() {
            return localizeLater("gui.Analyzer.AddressCopied");
        }

        public static TranslationTextComponent ChargerSpeed(double value) { return localizeLater("gui.Analyzer.ChargerSpeed", (int)(value * 100) + "%"); }

        public static TranslationTextComponent ComponentName(String value) { return localizeLater("gui.Analyzer.ComponentName", value); }

        public static TranslationTextComponent Components(int count, int maxCount) { return localizeLater("gui.Analyzer.Components", count + "/" + maxCount); }

        public static TranslationTextComponent LastError(String value) { return localizeLater("gui.Analyzer.LastError", localizeLater(value)); }

        public static TranslationTextComponent RobotOwner(String owner) { return localizeLater("gui.Analyzer.RobotOwner", owner); }

        public static TranslationTextComponent RobotName(String name) { return localizeLater("gui.Analyzer.RobotName", name); }

        public static TranslationTextComponent RobotXp(double experience, int level) { return localizeLater("gui.Analyzer.RobotXp", String.format("%.2f", experience), level); }

        public static TranslationTextComponent StoredEnergy(String value) { return localizeLater("gui.Analyzer.StoredEnergy", value); }

        public static TranslationTextComponent TotalEnergy(String value) { return localizeLater("gui.Analyzer.TotalEnergy", value); }

        public static TranslationTextComponent Users(Iterable<String> list) { return localizeLater("gui.Analyzer.Users", String.join(", ", list)); }

        public static TranslationTextComponent WirelessStrength(double value) { return localizeLater("gui.Analyzer.WirelessStrength", ((Integer)(int)value).toString()); }
    }

    public static class Assembler {

        public static String InsertTemplate() { return localizeImmediately("gui.Assembler.InsertCase"); }

        public static String CollectResult() { return localizeImmediately("gui.Assembler.Collect"); }

        public static TranslationTextComponent InsertCPU() { return localizeLater("gui.Assembler.InsertCPU"); }

        public static TranslationTextComponent InsertRAM() { return localizeLater("gui.Assembler.InsertRAM"); }



        public static ITextComponent Complexity(int complexity, int maxComplexity) {
            TranslationTextComponent message = localizeLater("gui.Assembler.Complexity", ((Integer)complexity).toString(), ((Integer)maxComplexity).toString());
            if (complexity > maxComplexity)
                return new StringTextComponent("§4").append(message);
            else
                return message;
        }

        public static String Run() {return localizeImmediately("gui.Assembler.Run"); }

        public static String Progress(double progress, String timeRemaining) { return localizeImmediately("gui.Assembler.Progress", ((Integer)(int)progress).toString(), timeRemaining); }

        public static ITextComponent Warning(String name) { return new StringTextComponent("§7- ").append(localizeLater("gui.Assembler.Warning." + name)); }

        public static TranslationTextComponent Warnings() { return localizeLater("gui.Assembler.Warnings"); }
    }

    public static class Chat {
        public static ITextComponent WarningLuaFallback() {return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.WarningLuaFallback")); }

        public static ITextComponent WarningProjectRed() {return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.WarningProjectRed")); }

        public static ITextComponent WarningRecipes() {return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.WarningRecipes")); }

        public static ITextComponent WarningClassTransformer() {return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.WarningClassTransformer")); }

        public static ITextComponent WarningLink(String url) { return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.WarningLink", url)); }

        public static ITextComponent InfoNewVersion(String version) { return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.NewVersion", version)); }

        public static ITextComponent TextureName(String name) { return new StringTextComponent("§aOpenComputers§f: ").append(localizeLater("gui.Chat.TextureName", name)); }
    }

    public static class Computer {
        public static String TurnOff() {return localizeImmediately("gui.Robot.TurnOff"); }

        public static String TurnOn() {return localizeImmediately("gui.Robot.TurnOn"); }

        public static String Power() {return localizeImmediately("gui.Robot.Power"); }
    }

    public static class Drive {
        public static String Managed() { return localizeImmediately("gui.Drive.Managed"); }

        public static String Unmanaged() { return localizeImmediately("gui.Drive.Unmanaged"); }

        public static String Warning() { return localizeImmediately("gui.Drive.Warning"); }

        public static String ReadOnlyLock() { return localizeImmediately("gui.Drive.ReadOnlyLock"); }

        public static String LockWarning() { return localizeImmediately("gui.Drive.ReadOnlyLockWarning"); }
    }

    public static class Raid {
        public static String Warning() { return localizeImmediately("gui.Raid.Warning"); }
    }

    public static class Rack {
        public static String Top() { return localizeImmediately("gui.Rack.Top"); }

        public static String Bottom() { return localizeImmediately("gui.Rack.Bottom"); }

        public static String Left() { return localizeImmediately("gui.Rack.Left"); }

        public static String Right() { return localizeImmediately("gui.Rack.Right"); }

        public static String Back() { return localizeImmediately("gui.Rack.Back"); }

        public static String None() { return localizeImmediately("gui.Rack.None"); }

        public static String RelayEnabled() { return localizeImmediately("gui.Rack.Enabled"); }

        public static String RelayDisabled() { return localizeImmediately("gui.Rack.Disabled"); }

        public static String RelayModeTooltip() { return localizeImmediately("gui.Rack.RelayModeTooltip"); }
    }

    public static class Switch {
        public static String TransferRate() { return localizeImmediately("gui.Switch.TransferRate"); }

        public static String PacketsPerCycle() { return localizeImmediately("gui.Switch.PacketsPerCycle"); }

        public static String QueueSize() { return localizeImmediately("gui.Switch.QueueSize"); }
    }

    public static class Terminal {
        public static TranslationTextComponent InvalidKey() { return localizeLater("gui.Terminal.InvalidKey"); }

        public static TranslationTextComponent OutOfRange() { return localizeLater("gui.Terminal.OutOfRange"); }
    }

    public static class Tooltip {
        public static String DiskUsage(long used, long capacity) { return localizeImmediately("tooltip.diskusage", ((Long)used).toString(), ((Long)capacity).toString()); }
        public static String DiskMode(boolean isUnmanaged) { if (isUnmanaged) return localizeImmediately("tooltip.diskmodeunmanaged"); else return localizeImmediately("tooltip.diskmodemanaged"); }
        public static String Materials() { return localizeImmediately("tooltip.materials"); }
        public static String DiskLock(String lockInfo) { if (lockInfo.isEmpty()) return ""; else return localizeImmediately("tooltip.disklocked", lockInfo); }
        public static String Tier(int tier) { return localizeImmediately("tooltip.tier", ((Integer)tier).toString()); }
        public static String PrintBeaconBase() { return localizeImmediately("tooltip.print.BeaconBase"); }
        public static String PrintLightValue(int level) {return localizeImmediately("tooltip.print.LightValue", ((Integer)level).toString()); }
        public static String PrintRedstoneLevel(int level) {return localizeImmediately("tooltip.print.RedstoneLevel", ((Integer)level).toString()); }
        public static String MFULinked(boolean isLinked) { if (isLinked) return localizeImmediately("tooltip.upgrademf.Linked"); else return localizeImmediately("tooltip.upgrademf.Unlinked"); }
        public static String ExperienceLevel(double level) { return localizeImmediately("tooltip.robot_level", ((Double)level).toString()); }
    }
}
