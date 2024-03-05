package li.cil.oc;

import com.mojang.authlib.GameProfile;
import com.typesafe.config.*;
import com.typesafe.config.impl.OpenComputersConfigCommentManipulationHook;
import li.cil.oc.api.internal.TextBuffer;
import li.cil.oc.common.Tier;
import li.cil.oc.server.component.DebugCard;
import li.cil.oc.util.InternetFilteringRule;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Settings {
    private static Config config;
    private Settings() {}
    public final double screenTextFadeStartDistance = config.getDouble("client.screenTextFadeStartDistance");
    public final double maxScreenTextRenderDistance = config.getDouble("client.maxScreenTextRenderDistance");
    public final boolean textLinearFiltering = config.getBoolean("client.textLinearFiltering");
    public final boolean textAntiAlias = config.getBoolean("client.textAntiAlias");
    public final boolean robotLabels = config.getBoolean("client.robotLabels");
    public final float soundVolume = Math.max(Math.min((float)config.getDouble("client.soundVolume"), 2.0f), 0.0f);
    public final double fontCharScale = Math.max(Math.min(config.getDouble("client.fontCharScale"), 2), 0.5);
    public final double hologramFadeStartDistance = Math.max(config.getDouble("client.hologramFadeStartDistance"), 0);
    public final double hologramRenderDistance = Math.max(config.getDouble("client.hologramRenderDistance"), 0);
    public final double hologramFlickerFrequency = Math.max(config.getDouble("client.hologramFlickerFrequency"), 0);
    public final Integer monochromeColor = Integer.decode(config.getString("client.monochromeColor"));
    public final String fontRenderer = config.getString("client.fontRenderer");
    public final int beepSampleRate = config.getInt("client.beepSampleRate");
    public final int beepAmplitude = Math.max(Math.min(config.getInt("client.beepVolume"), Byte.MAX_VALUE), 0);
    public final float beepRadius = Math.max(Math.min((float)config.getDouble("client.beepRadius"), 32.0f), 1.0f);
    public double[] nanomachineHudPos;
    {
        List<Double> nanomachineHudPosList = config.getDoubleList("client.nanomachineHudPos");
        if (nanomachineHudPosList.size() == 2) {
            nanomachineHudPos = nanomachineHudPosList.stream().mapToDouble(d->d).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of HUD coordiantes, ignoring.");
            nanomachineHudPos = new double[] {-1.0, -1.0};
        }
    }
    public final boolean enableNanomachinePfx = config.getBoolean("client.enableNanomachinePfx");
    public final int transposerFluidTransferRate = config.getInt("misc.transposerFluidTransferRate");

    // ----------------------------------------------------------------------- //
    // computer
    public final int threads = Math.max(config.getInt("computer.threads"), 1);
    public final double timeout = Math.max(config.getDouble("computer.timeout"), 0);
    public final double startupDelay = Math.max(config.getDouble("computer.startupDelay"), 0.05);
    public final int eepromSize = Math.max(config.getInt("computer.eepromSize"), 0);
    public final int eepromDataSize = Math.max(config.getInt("computer.eepromDataSize"), 0);

    public int[] cpuComponentSupport;
    {
        List<Integer> cpuComponentSupportList = config.getIntList("computer.cpuComponentCount");
        if (cpuComponentSupportList.size() == 4) {
            cpuComponentSupport = cpuComponentSupportList.stream().mapToInt(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of CPU component counts, ignoring.");
            cpuComponentSupport = new int[] {8, 12, 16, 1024};
        }
    }
    public final double[] callBudgets;
    {
        List<Double> callBudgetsList = config.getDoubleList("computer.callBudgets");
        if (callBudgetsList.size() == 3) {
            callBudgets = callBudgetsList.stream().mapToDouble(d->d).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of call budgets, ignoring.");
            callBudgets = new double[] {0.5, 1.0, 1.5};
        }
    }
    public final boolean canComputersBeOwned = config.getBoolean("computer.canComputersBeOwned");
    public final int maxUsers = Math.max(config.getInt("computer.maxUsers"), 0);
    public final int maxUsernameLength = Math.max(config.getInt("computer.maxUsernameLength"), 0);
    public final boolean eraseTmpOnReboot = config.getBoolean("computer.eraseTmpOnReboot");
    public final int executionDelay = Math.max(config.getInt("computer.executionDelay"), 0);

    // computer.lua
    public final boolean allowBytecode = config.getBoolean("computer.lua.allowBytecode");
    public final boolean allowGC = config.getBoolean("computer.lua.allowGC");
    public final boolean enableLua53 = config.getBoolean("computer.lua.enableLua53");
    public final boolean defaultLua53 = config.getBoolean("computer.lua.defaultLua53");
    public final boolean enableLua54 = config.getBoolean("computer.lua.enableLua54");
    public final int[] ramSizes;
    {
        List<Integer> ramSizesList = config.getIntList("computer.lua.ramSizes");
        if (ramSizesList.size() == 6) {
            ramSizes = ramSizesList.stream().mapToInt(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of RAM sizes, ignoring.");
            ramSizes = new int[] {192, 256, 384, 512, 768, 1024};
        }
    }
    public final double ramScaleFor64Bit = Math.max(config.getDouble("computer.lua.ramScaleFor64Bit"), 1.0f);
    public final int maxTotalRam = Math.max(config.getInt("computer.lua.maxTotalRam"), 0);

    // ----------------------------------------------------------------------- //
    // robot
    public final boolean allowActivateBlocks = config.getBoolean("robot.allowActivateBlocks");
    public final boolean allowUseItemsWithDuration = config.getBoolean("robot.allowUseItemsWithDuration");
    public final boolean canAttackPlayers = config.getBoolean("robot.canAttackPlayers");
    public final int limitFlightHeight = Math.max(config.getInt("robot.limitFlightHeight"), -1);
    public final boolean screwCobwebs = config.getBoolean("robot.notAfraidOfSpiders");
    public final double swingRange = config.getDouble("robot.swingRange");
    public final double useAndPlaceRange = config.getDouble("robot.useAndPlaceRange");
    public final double itemDamageRate = Math.min(Math.max(config.getDouble("robot.itemDamageRate"), 0), 1);
    public final String nameFormat = config.getString("robot.nameFormat");
    public final String uuidFormat = config.getString("robot.uuidFormat");
    public final int[] upgradeFlightHeight;
    {
        List<Integer> upgradeFlightHeightList = config.getIntList("robot.upgradeFlightHeight");
        if (upgradeFlightHeightList.size() == 2) {
            upgradeFlightHeight = upgradeFlightHeightList.stream().mapToInt(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of hover flight height counts, ignoring.");
            upgradeFlightHeight = new int[] {64, 256};
        }
    }

    // robot.xp
    public final double baseXpToLevel = Math.max(config.getDouble("robot.xp.baseValue"), 0);
    public final double constantXpGrowth = Math.max(config.getDouble("robot.xp.constantGrowth"), 1);
    public final double exponentialXpGrowth = Math.max(config.getDouble("robot.xp.exponentialGrowth"), 1);
    public final double robotActionXp = Math.max(config.getDouble("robot.xp.actionXp"), 0);
    public final double robotExhaustionXpRate = Math.max(config.getDouble("robot.xp.exhaustionXpRate"), 0);
    public final double robotOreXpRate = Math.max(config.getDouble("robot.xp.oreXpRate"), 0);
    public final double bufferPerLevel = Math.max(config.getDouble("robot.xp.bufferPerLevel"), 0);
    public final double toolEfficiencyPerLevel = Math.max(config.getDouble("robot.xp.toolEfficiencyPerLevel"), 0);
    public final double harvestSpeedBoostPerLevel = Math.max(config.getDouble("robot.xp.harvestSpeedBoostPerLevel"), 0);

    // ----------------------------------------------------------------------- //
    // robot.delays

    // Note: all delays are reduced by one tick to account for the tick they are
    // performed in (since all actions are delegated to the server thread).
    public final double turnDelay = Math.max((config.getDouble("robot.delays.turn") - 0.06), 0.05);
    public final double moveDelay = Math.max((config.getDouble("robot.delays.move") - 0.06), 0.05);
    public final double swingDelay = Math.max((config.getDouble("robot.delays.swing") - 0.06), 0);
    public final double useDelay = Math.max((config.getDouble("robot.delays.use") - 0.06), 0);
    public final double placeDelay = Math.max((config.getDouble("robot.delays.place") - 0.06), 0);
    public final double dropDelay = Math.max((config.getDouble("robot.delays.drop") - 0.06), 0);
    public final double suckDelay = Math.max((config.getDouble("robot.delays.suck") - 0.06), 0);
    public final double harvestRatio = Math.max(config.getDouble("robot.delays.harvestRatio"), 0);

    // ----------------------------------------------------------------------- //
    // power
    public final boolean ignorePower = config.getBoolean("power.ignorePower");
    public final double tickFrequency = Math.max(config.getDouble("power.tickFrequency"), 1);
    public final double chargeRateExternal = config.getDouble("power.chargerChargeRate");
    public final double chargeRateTablet = config.getDouble("power.chargerChargeRateTablet");
    public final double generatorEfficiency = config.getDouble("power.generatorEfficiency");
    public final double solarGeneratorEfficiency = config.getDouble("power.solarGeneratorEfficiency");
    public final double assemblerTickAmount = Math.max(config.getDouble("power.assemblerTickAmount"), 1);
    public final double disassemblerTickAmount = Math.max(config.getDouble("power.disassemblerTickAmount"), 1);
    public final double printerTickAmount = Math.max(config.getDouble("power.printerTickAmount"), 1);
    public final List<String> powerModBlacklist = config.getStringList("power.modBlacklist");

    // power.carpetedCapacitors
    public final double sheepPower = Math.max(config.getDouble("power.carpetedCapacitors.sheepPower"), 0);
    public final double ocelotPower = Math.max(config.getDouble("power.carpetedCapacitors.ocelotPower"), 0);
    public final double carpetDamageChance = Math.min(Math.max(config.getDouble("power.carpetedCapacitors.damageChance"), 0), 1.0);

    // power.buffer
    public final double bufferCapacitor = Math.max(config.getDouble("power.buffer.capacitor"), 0);
    public final double bufferCapacitorAdjacencyBonus = Math.max(config.getDouble("power.buffer.capacitorAdjacencyBonus"), 0);
    public final double bufferComputer = Math.max(config.getDouble("power.buffer.computer"), 0);
    public final double bufferRobot = Math.max(config.getDouble("power.buffer.robot"), 0);
    public final double bufferConverter = Math.max(config.getDouble("power.buffer.converter"), 0);
    public final double bufferDistributor = Math.max(config.getDouble("power.buffer.distributor"), 0);
    public final double[] bufferCapacitorUpgrades;
    {
        List<Double> bufferCapacitorUpgradesList = config.getDoubleList("power.buffer.batteryUpgrades");
        if (bufferCapacitorUpgradesList.size() == 3) {
            bufferCapacitorUpgrades = bufferCapacitorUpgradesList.stream().mapToDouble(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of battery upgrade buffer sizes, ignoring.");
            bufferCapacitorUpgrades = new double[] {10000.0, 15000.0, 20000.0};
        }
    }
    public final double bufferTablet = Math.max(config.getDouble("power.buffer.tablet"), 0);
    public final double bufferAccessPoint = Math.max(config.getDouble("power.buffer.accessPoint"), 0);
    public final double bufferDrone = Math.max(config.getDouble("power.buffer.drone"), 0);
    public final double bufferMicrocontroller = Math.max(config.getDouble("power.buffer.mcu"), 0);
    public final double bufferHoverBoots = Math.max(config.getDouble("power.buffer.hoverBoots"), 1);
    public final double bufferNanomachines = Math.max(config.getDouble("power.buffer.nanomachines"), 0);

    // power.cost
    public final double computerCost = Math.max(config.getDouble("power.cost.computer"), 0);
    public final double microcontrollerCost = Math.max(config.getDouble("power.cost.microcontroller"), 0);
    public final double robotCost = Math.max(config.getDouble("power.cost.robot"), 0);
    public final double droneCost = Math.max(config.getDouble("power.cost.drone"), 0);
    public final double sleepCostFactor = Math.max(config.getDouble("power.cost.sleepFactor"), 0);
    public final double screenCost = Math.max(config.getDouble("power.cost.screen"), 0);
    public final double hologramCost = Math.max(config.getDouble("power.cost.hologram"), 0);
    public final double hddReadCost = Math.max(config.getDouble("power.cost.hddRead"), 0) / 1024;
    public final double hddWriteCost = Math.max(config.getDouble("power.cost.hddWrite"), 0) / 1024;
    public final double gpuSetCost = Math.max(config.getDouble("power.cost.gpuSet"), 0) / basicScreenPixels();
    public final double gpuFillCost = Math.max(config.getDouble("power.cost.gpuFill"), 0) / basicScreenPixels();
    public final double gpuClearCost = Math.max(config.getDouble("power.cost.gpuClear"), 0) / basicScreenPixels();
    public final double gpuCopyCost = Math.max(config.getDouble("power.cost.gpuCopy"), 0) / basicScreenPixels();
    public final double robotTurnCost = Math.max(config.getDouble("power.cost.robotTurn"), 0);
    public final double robotMoveCost = Math.max(config.getDouble("power.cost.robotMove"), 0);
    public final double robotExhaustionCost = Math.max(config.getDouble("power.cost.robotExhaustion"), 0);
    public final double[] wirelessCostPerRange;
    {
        List<Double> wirelessCostPerRangeList = config.getDoubleList("power.cost.wirelessCostPerRange");
        if (wirelessCostPerRangeList.size() == 2) {
            wirelessCostPerRange = wirelessCostPerRangeList.stream().mapToDouble(d->Math.max(d, 0.0)).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of wireless card energy costs, ignoring.");
            wirelessCostPerRange = new double[] {0.05, 0.05};
        }
    }
    public final double abstractBusPacketCost = Math.max(config.getDouble("power.cost.abstractBusPacket"), 0);
    public final double geolyzerScanCost = Math.max(config.getDouble("power.cost.geolyzerScan"), 0);
    public final double robotBaseCost = Math.max(config.getDouble("power.cost.robotAssemblyBase"), 0);
    public final double robotComplexityCost = Math.max(config.getDouble("power.cost.robotAssemblyComplexity"), 0);
    public final double microcontrollerBaseCost = Math.max(config.getDouble("power.cost.microcontrollerAssemblyBase"), 0);
    public final double microcontrollerComplexityCost = Math.max(config.getDouble("power.cost.microcontrollerAssemblyComplexity"), 0);
    public final double tabletBaseCost = Math.max(config.getDouble("power.cost.tabletAssemblyBase"), 0);
    public final double tabletComplexityCost = Math.max(config.getDouble("power.cost.tabletAssemblyComplexity"), 0);
    public final double droneBaseCost = Math.max(config.getDouble("power.cost.droneAssemblyBase"), 0);
    public final double droneComplexityCost = Math.max(config.getDouble("power.cost.droneAssemblyComplexity"), 0);
    public final double disassemblerItemCost = Math.max(config.getDouble("power.cost.disassemblerPerItem"), 0);
    public final double chunkloaderCost = Math.max(config.getDouble("power.cost.chunkloaderCost"), 0);
    public final double pistonCost = Math.max(config.getDouble("power.cost.pistonPush"), 0);
    public final double eepromWriteCost = Math.max(config.getDouble("power.cost.eepromWrite"), 0);
    public final double printCost = Math.max(config.getDouble("power.cost.printerModel"), 0);
    public final double hoverBootJump = Math.max(config.getDouble("power.cost.hoverBootJump"), 0);
    public final double hoverBootAbsorb = Math.max(config.getDouble("power.cost.hoverBootAbsorb"), 0);
    public final double hoverBootMove = Math.max(config.getDouble("power.cost.hoverBootMove"), 0);
    public final double dataCardTrivial = Math.max(config.getDouble("power.cost.dataCardTrivial"), 0);
    public final double dataCardTrivialByte = Math.max(config.getDouble("power.cost.dataCardTrivialByte"), 0);
    public final double dataCardSimple = Math.max(config.getDouble("power.cost.dataCardSimple"), 0);
    public final double dataCardSimpleByte = Math.max(config.getDouble("power.cost.dataCardSimpleByte"), 0);
    public final double dataCardComplex = Math.max(config.getDouble("power.cost.dataCardComplex"), 0);
    public final double dataCardComplexByte = Math.max(config.getDouble("power.cost.dataCardComplexByte"), 0);
    public final double dataCardAsymmetric = Math.max(config.getDouble("power.cost.dataCardAsymmetric"), 0);
    public final double transposerCost = Math.max(config.getDouble("power.cost.transposer"), 0);
    public final double nanomachineCost = Math.max(config.getDouble("power.cost.nanomachineInput"), 0);
    public final double nanomachineReconfigureCost = Math.max(config.getDouble("power.cost.nanomachinesReconfigure"), 0);
    public final double mfuCost = Math.max(config.getDouble("power.cost.mfuRelay"), 0);

    // power.rate
    public final double accessPointRate = Math.max(config.getDouble("power.rate.accessPoint"), 0);
    public final double assemblerRate = Math.max(config.getDouble("power.rate.assembler"), 0);
    public final double[] caseRate;
    {
        List<Double> caseRateList = config.getDoubleList("power.rate.case");
        if (caseRateList.size() == 3) {
            caseRate = new double[] { caseRateList.get(0), caseRateList.get(1), caseRateList.get(2), 9001.0 };
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of computer case conversion rates, ignoring.");
            caseRate = new double[] {5.0, 10.0, 20.0, 9001.0};
        }
    }
    // Creative case.
    public final double chargerRate = Math.max(config.getDouble("power.rate.charger"), 0);
    public final double disassemblerRate = Math.max(config.getDouble("power.rate.disassembler"), 0);
    public final double powerConverterRate = Math.max(config.getDouble("power.rate.powerConverter"), 0);
    public final double serverRackRate = Math.max(config.getDouble("power.rate.serverRack"), 0);

    // power.value
    private final double valueAppliedEnergistics2 = config.getDouble("power.value.AppliedEnergistics2");
    private final double valueFactorization = config.getDouble("power.value.Factorization");
    private final double valueGalacticraft = config.getDouble("power.value.Galacticraft");
    private final double valueIndustrialCraft2 = config.getDouble("power.value.IndustrialCraft2");
    private final double valueMekanism = config.getDouble("power.value.Mekanism");
    private final double valuePowerAdvantage = config.getDouble("power.value.PowerAdvantage");
    private final double valueRedstoneFlux = config.getDouble("power.value.RedstoneFlux");
    private final double valueRotaryCraft = config.getDouble("power.value.RotaryCraft") / 11256.0;
    private final double valueForgeEnergy;
    {
        if (config.hasPath("power.value.ForgeEnergy"))
            valueForgeEnergy = config.getDouble("power.value.ForgeEnergy");
        else
            valueForgeEnergy = valueRedstoneFlux;
    }

    private final double valueInternal = 1000.0;

    public final double ratioAppliedEnergistics2 = valueAppliedEnergistics2 / valueInternal;
    public final double ratioFactorization = valueFactorization / valueInternal;
    public final double ratioGalacticraft = valueGalacticraft / valueInternal;
    public final double ratioIndustrialCraft2 = valueIndustrialCraft2 / valueInternal;
    public final double ratioMekanism = valueMekanism / valueInternal;
    public final double ratioPowerAdvantage = valuePowerAdvantage / valueInternal;
    public final double ratioRedstoneFlux = valueRedstoneFlux / valueInternal;
    public final double ratioRotaryCraft = valueRotaryCraft / valueInternal;
    public final double ratioForgeEnergy = valueForgeEnergy / valueInternal;

    // ----------------------------------------------------------------------- //
    // filesystem
    public final int fileCost = Math.max(config.getInt("filesystem.fileCost"), 0);
    public final boolean bufferChanges = config.getBoolean("filesystem.bufferChanges");
    public final int[] hddSizes;
    {
        List<Integer> hddSizesList = config.getIntList("filesystem.hddSizes");
        if (hddSizesList.size() == 3) {
            hddSizes = hddSizesList.stream().mapToInt(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of HDD sizes, ignoring.");
            hddSizes = new int[] {1024, 2048, 4096};
        }
    }
    public final int[] hddPlatterCounts;
    {
        List<Integer> hddPlatterCountsList = config.getIntList("filesystem.hddPlatterCounts");
        if (hddPlatterCountsList.size() == 3) {
            hddPlatterCounts = hddPlatterCountsList.stream().mapToInt(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of HDD platter counts, ignoring.");
            hddPlatterCounts = new int[] {2, 4, 6};
        }
    }
    public final int floppySize = Math.max(config.getInt("filesystem.floppySize"), 0);
    public final int tmpSize = Math.max(config.getInt("filesystem.tmpSize"), 0);
    public final int maxHandles = Math.max(config.getInt("filesystem.maxHandles"), 0);
    public final int maxReadBuffer = Math.max(config.getInt("filesystem.maxReadBuffer"), 0);
    public final int sectorSeekThreshold = config.getInt("filesystem.sectorSeekThreshold");
    public final double sectorSeekTime = config.getDouble("filesystem.sectorSeekTime");

    // ----------------------------------------------------------------------- //
    // internet
    public final boolean httpEnabled = config.getBoolean("internet.enableHttp");
    public final boolean httpHeadersEnabled = config.getBoolean("internet.enableHttpHeaders");
    public final boolean tcpEnabled = config.getBoolean("internet.enableTcp");
    public final InternetFilteringRule[] internetFilteringRules;
    {
        List<String> filteringRulesList = config.getStringList("internet.filteringRules");
        internetFilteringRules = filteringRulesList.stream()
                .filter(p -> !p.equals("removeme"))
                .map(InternetFilteringRule::new)
                .toArray(InternetFilteringRule[]::new);
    }
    public final boolean internetFilteringRulesObserved = !config.getStringList("internet.filteringRules").contains("removeme");
    public final int httpTimeout = Math.max(config.getInt("internet.requestTimeout"), 0) * 1000;
    public final int maxConnections = Math.max(config.getInt("internet.maxTcpConnections"), 0);
    public final int internetThreads = Math.max(config.getInt("internet.threads"), 1);

    // ----------------------------------------------------------------------- //
    // switch
    public final int switchDefaultMaxQueueSize = Math.max(config.getInt("switch.defaultMaxQueueSize"), 1);
    public final int switchQueueSizeUpgrade = Math.max(config.getInt("switch.queueSizeUpgrade"), 0);
    public final int switchDefaultRelayDelay = Math.max(config.getInt("switch.defaultRelayDelay"), 1);
    public final double switchRelayDelayUpgrade = Math.max(config.getDouble("switch.relayDelayUpgrade"), 0);
    public final int switchDefaultRelayAmount = Math.max(config.getInt("switch.defaultRelayAmount"), 1);
    public final int switchRelayAmountUpgrade = Math.max(config.getInt("switch.relayAmountUpgrade"), 0);

    // ----------------------------------------------------------------------- //
    // hologram
    public final double[] hologramMaxScaleByTier;
    {
        List<Double> hologramMaxScaleByTierList = config.getDoubleList("hologram.maxScale");
        if (hologramMaxScaleByTierList.size() == 2) {
            hologramMaxScaleByTier = hologramMaxScaleByTierList.stream().mapToDouble(d->Math.max(d, 1.0)).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of hologram max scales, ignoring.");
            hologramMaxScaleByTier = new double[] {3.0f, 4.0f};
        }
    }
    public final double[] hologramMaxTranslationByTier;
    {
        List<Double> hologramMaxTranslationByTierList = config.getDoubleList("hologram.maxTranslation");
        if (hologramMaxTranslationByTierList.size() == 2) {
            hologramMaxTranslationByTier = hologramMaxTranslationByTierList.stream().mapToDouble(d->Math.max(d, 0.0)).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of hologram max translations, ignoring.");
            hologramMaxTranslationByTier = new double[] {0.25f, 0.5f};
        }
    }
    public final double hologramSetRawDelay = Math.max(config.getDouble("hologram.setRawDelay"), 0);
    public final boolean hologramLight = config.getBoolean("hologram.emitLight");

    // ----------------------------------------------------------------------- //
    // misc
    public final int maxScreenWidth = Math.max(config.getInt("misc.maxScreenWidth"), 1);
    public final int maxScreenHeight = Math.max(config.getInt("misc.maxScreenHeight"), 1);
    public final boolean inputUsername = config.getBoolean("misc.inputUsername");
    public final int maxNetworkPacketSize = Math.max(config.getInt("misc.maxNetworkPacketSize"), 0);
    // Need at least 4 for nanomachine protocol. Because I can!
    public final int maxNetworkPacketParts = Math.max(config.getInt("misc.maxNetworkPacketParts"), 4);

    public final int[] maxOpenPorts;
    {
        List<Integer> maxOpenPortsList = config.getIntList("misc.maxOpenPorts");
        if (maxOpenPortsList.size() == 3) {
            maxOpenPorts = maxOpenPortsList.stream().mapToInt(i->Math.max(i, 0)).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of max open ports, ignoring.");
            maxOpenPorts = new int[] {16, 1, 16};
        }
    }

    public final double[] maxWirelessRange;
    {
        List<Double> maxWirelessRangeList = config.getDoubleList("misc.maxWirelessRange");
        if (maxWirelessRangeList.size() == 2) {
            maxWirelessRange = maxWirelessRangeList.stream().mapToDouble(d->Math.max(d, 0.0)).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of wireless card max ranges, ignoring.");
            maxWirelessRange = new double[] {16.0f, 400.0f};
        }
    }
    public final int rTreeMaxEntries = 10;
    public final int terminalsPerServer = 4;
    public final boolean updateCheck = config.getBoolean("misc.updateCheck");
    public final int lootProbability = config.getInt("misc.lootProbability");
    public final boolean lootRecrafting = config.getBoolean("misc.lootRecrafting");
    public final int geolyzerRange = config.getInt("misc.geolyzerRange");
    public final float geolyzerNoise = Math.max((float)config.getDouble("misc.geolyzerNoise"), 0);
    public final boolean disassembleAllTheThings = config.getBoolean("misc.disassembleAllTheThings");
    public final double disassemblerBreakChance = Math.min(Math.max(config.getDouble("misc.disassemblerBreakChance"), 0), 1);
    public final List<String> disassemblerInputBlacklist = config.getStringList("misc.disassemblerInputBlacklist");
    public final boolean hideOwnPet = config.getBoolean("misc.hideOwnSpecial");
    public final boolean allowItemStackInspection = config.getBoolean("misc.allowItemStackInspection");
    public final int[] databaseEntriesPerTier = new int[] {9, 25, 81};
    // Not configurable because of GUI design.
    public final double presentChance = Math.min(Math.max(config.getDouble("misc.presentChance"), 0), 1);
    public final List<String> assemblerBlacklist = config.getStringList("misc.assemblerBlacklist");
    public final int threadPriority = config.getInt("misc.threadPriority");
    public final boolean giveManualToNewPlayers = config.getBoolean("misc.giveManualToNewPlayers");
    public final int dataCardSoftLimit = Math.max(config.getInt("misc.dataCardSoftLimit"), 0);
    public final int dataCardHardLimit = Math.max(config.getInt("misc.dataCardHardLimit"), 0);
    public final double dataCardTimeout = Math.max(config.getDouble("misc.dataCardTimeout"), 0.0f);
    public final int serverRackSwitchTier = Math.min(Math.max(config.getInt("misc.serverRackSwitchTier") - 1, Tier.None), Tier.Three);
    public final double redstoneDelay = Math.max(config.getDouble("misc.redstoneDelay"), 0.0f);
    public final double tradingRange = Math.max(config.getDouble("misc.tradingRange"), 0.0f);
    public final int mfuRange = Math.min(Math.max(config.getInt("misc.mfuRange"), 0), 128);

    // ----------------------------------------------------------------------- //
    // nanomachines
    public final double nanomachineTriggerQuota = Math.max(config.getDouble("nanomachines.triggerQuota"), 0);
    public final double nanomachineConnectorQuota = Math.max(config.getDouble("nanomachines.connectorQuota"), 0);
    public final int nanomachineMaxInputs = Math.max(config.getInt("nanomachines.maxInputs"), 1);
    public final int nanomachineMaxOutputs = Math.max(config.getInt("nanomachines.maxOutputs"), 1);
    public final int nanomachinesSafeInputsActive = Math.max(config.getInt("nanomachines.safeInputsActive"), 0);
    public final int nanomachinesMaxInputsActive = Math.max(config.getInt("nanomachines.maxInputsActive"), 0);
    public final double nanomachinesCommandDelay = Math.max(config.getDouble("nanomachines.commandDelay"), 0);
    public final double nanomachinesCommandRange = Math.max(config.getDouble("nanomachines.commandRange"), 0);
    public final double nanomachineMagnetRange = Math.max(config.getDouble("nanomachines.magnetRange"), 0);
    public final int nanomachineDisintegrationRange = Math.max(config.getInt("nanomachines.disintegrationRange"), 0);
    public final List<?> nanomachinePotionWhitelist = config.getAnyRefList("nanomachines.potionWhitelist");
    public final float nanomachinesHungryDamage = Math.max((float)config.getDouble("nanomachines.hungryDamage"), 0);
    public final double nanomachinesHungryEnergyRestored = Math.max(config.getDouble("nanomachines.hungryEnergyRestored"), 0);

    // ----------------------------------------------------------------------- //
    // printer
    public final int maxPrintComplexity = config.getInt("printer.maxShapes");
    public final double printRecycleRate = config.getDouble("printer.recycleRate");
    public final boolean chameliumEdible = config.getBoolean("printer.chameliumEdible");
    public final int maxPrintLightLevel = Math.min(Math.max(config.getInt("printer.maxBaseLightLevel"), 0), 15);
    public final int printCustomRedstone = Math.max(config.getInt("printer.customRedstoneCost"), 0);
    public final int printMaterialValue = Math.max(config.getInt("printer.materialValue"), 0);
    public final int printInkValue = Math.max(config.getInt("printer.inkValue"), 0);
    public final boolean printsHaveOpacity = config.getBoolean("printer.printsHaveOpacity");
    public final double noclipMultiplier = Math.max(config.getDouble("printer.noclipMultiplier"), 0);

    // chunkloader
    public final List<Integer> chunkloadDimensionBlacklist = Settings.getIntList(config, "chunkloader.dimBlacklist", Optional.empty());
    public final List<Integer> chunkloadDimensionWhitelist = Settings.getIntList(config, "chunkloader.dimWhitelist", Optional.empty());

    // ----------------------------------------------------------------------- //
    // integration
    public final List<String> modBlacklist = config.getStringList("integration.modBlacklist");
    public final List<String> peripheralBlacklist = config.getStringList("integration.peripheralBlacklist");
    public final String fakePlayerUuid = config.getString("integration.fakePlayerUuid");
    public final String fakePlayerName = config.getString("integration.fakePlayerName");
    public final GameProfile fakePlayerProfile = new GameProfile(UUID.fromString(fakePlayerUuid), fakePlayerName);

    // integration.vanilla
    public final boolean enableInventoryDriver = config.getBoolean("integration.vanilla.enableInventoryDriver");
    public final boolean enableTankDriver = config.getBoolean("integration.vanilla.enableTankDriver");
    public final boolean enableCommandBlockDriver = config.getBoolean("integration.vanilla.enableCommandBlockDriver");
    public final boolean allowItemStackNBTTags = config.getBoolean("integration.vanilla.allowItemStackNBTTags");

    // integration.buildcraft
    public final double costProgrammingTable = Math.max(config.getDouble("integration.buildcraft.programmingTableCost"), 0.0f);

    // ----------------------------------------------------------------------- //
    // debug
    public final boolean logLuaCallbackErrors = config.getBoolean("debug.logCallbackErrors");
    public final boolean forceLuaJ = config.getBoolean("debug.forceLuaJ");
    public final boolean allowUserdata = !config.getBoolean("debug.disableUserdata");
    public final boolean allowPersistence = !config.getBoolean("debug.disablePersistence");
    public final boolean limitMemory = !config.getBoolean("debug.disableMemoryLimit");
    public final boolean forceCaseInsensitive = config.getBoolean("debug.forceCaseInsensitiveFS");
    public final boolean logFullLibLoadErrors = config.getBoolean("debug.logFullNativeLibLoadErrors");
    public final String forceNativeLibPlatform = config.getString("debug.forceNativeLibPlatform");
    public final String forceNativeLibPathFirst = config.getString("debug.forceNativeLibPathFirst");
    public final boolean logOpenGLErrors = config.getBoolean("debug.logOpenGLErrors");
    public final boolean logHexFontErrors = config.getBoolean("debug.logHexFontErrors");
    public final boolean alwaysTryNative = config.getBoolean("debug.alwaysTryNative");
    public final boolean debugPersistence = config.getBoolean("debug.verbosePersistenceErrors");
    public final boolean nativeInTmpDir = config.getBoolean("debug.nativeInTmpDir");
    public final boolean periodicallyForceLightUpdate = config.getBoolean("debug.periodicallyForceLightUpdate");
    public final boolean insertIdsInConverters = config.getBoolean("debug.insertIdsInConverters");
    public final Settings.DebugCardAccess debugCardAccess;
    {
        Object unwrapped = config.getValue("debug.debugCardAccess").unwrapped();
        if (unwrapped.equals("true") || unwrapped.equals("allow") || unwrapped.equals(Boolean.TRUE)) {
            debugCardAccess = new Settings.DebugCardAccess.Allowed();
        } else if (unwrapped.equals("false") || unwrapped.equals("deny") || unwrapped.equals(Boolean.FALSE)) {
            debugCardAccess = new Settings.DebugCardAccess.Forbidden();
        } else if (unwrapped.equals("whitelist")) {
            File wlFile = FMLPaths.CONFIGDIR.get().resolve(Paths.get("opencomputers", "debug_card_whitelist.txt")).toFile();
            debugCardAccess = new Settings.DebugCardAccess.Whitelist(wlFile);
        } else { // Fallback to most secure configuration
            OpenComputers$.MODULE$.log().warn("Unknown debug card access type, falling back to `deny`. Allowed values: `allow`, `deny`, `whitelist`.");
            debugCardAccess = new Settings.DebugCardAccess.Forbidden();
        }
    }

    public final boolean registerLuaJArchitecture = config.getBoolean("debug.registerLuaJArchitecture");
    public final boolean disableLocaleChanging = config.getBoolean("debug.disableLocaleChanging");

    // >= 1.7.4

    public final int maxSignalQueueSize;
    {
        if (config.hasPath("computer.maxSignalQueueSize"))
            maxSignalQueueSize = Math.max(config.getInt("computer.maxSignalQueueSize"), 256);
        else
            maxSignalQueueSize = 256;
    }

    // >= 1.7.6
    public final double[] vramSizes;
    {
        List<Double> vramSizesList = config.getDoubleList("gpu.vramSizes");
        if (vramSizesList.size() == 3) {
            vramSizes = vramSizesList.stream().mapToDouble(i->i).toArray();
        } else {
            OpenComputers$.MODULE$.log().warn("Bad number of VRAM sizes (expected 3), ignoring.");
            vramSizes = new double[] {1.0f, 2.0f, 3.0f};
        }
    }
    public final double bitbltCost = config.hasPath("gpu.bitbltCost") ? config.getDouble("gpu.bitbltCost") : 0.5f;

    // >= 1.8.2
    public final int diskActivitySoundDelay = Math.max(config.getInt("misc.diskActivitySoundDelay"), -1);
    public final double maxNetworkClientPacketDistance = Math.max(config.getDouble("misc.maxNetworkClientPacketDistance"), 0);
    public final double maxNetworkClientEffectPacketDistance = Math.max(config.getDouble("misc.maxNetworkClientEffectPacketDistance"), 0);
    public final double maxNetworkClientSoundPacketDistance = Math.max(config.getDouble("misc.maxNetworkClientSoundPacketDistance"), 0);

    public boolean internetFilteringRulesInvalid() {
        return Arrays.stream(internetFilteringRules).anyMatch(InternetFilteringRule::invalid);
    }

    public boolean internetAccessConfigured() {
        return httpEnabled || tcpEnabled;
    }

    public boolean internetAccessAllowed() {
        return internetAccessConfigured() && !internetFilteringRulesInvalid();
    }

    // Port of scala singleton object
    public static final String resourceDomain = OpenComputers$.MODULE$.ID();
    public static final String namespace = "oc:";
    public static final String savePath = "opencomputers/";
    public static final String scriptPath = "/assets/" + resourceDomain + "/lua/";
    public static final List<Map.Entry<Integer, Integer>> screenResolutionsByTier = Arrays.asList(new AbstractMap.SimpleEntry<>(50, 16), new AbstractMap.SimpleEntry<>(80, 25), new AbstractMap.SimpleEntry<>(160, 50));
    public static final TextBuffer.ColorDepth[] screenDepthsByTier = new TextBuffer.ColorDepth[] {TextBuffer.ColorDepth.OneBit, TextBuffer.ColorDepth.FourBit, TextBuffer.ColorDepth.EightBit};
    public static final int[] deviceComplexityByTier = new int[]{12, 24, 32, 9001};
    public static final boolean rTreeDebugRenderer = false;
    public static final int blockRenderId = -1;
    private static final List<String> forbiddenConfigLists = Arrays.asList(
        /* 1.8.3+ filtering rules migration */
        "internet.blacklist", "internet.whitelist"
    );
    private static final String prefix = "opencomputers.";

    public static int basicScreenPixels() {
        return screenResolutionsByTier.get(0).getKey() * screenResolutionsByTier.get(0).getValue();
    }

    private static Settings settings = null;

    public static Settings get() {
        return settings;
    }

    public Config getConfig() {
        return config;
    }

    public static void load(File file) {
        Config defaults = loadDefaults();
        Config config;

        try {
            String plain = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            config = patchConfig(ConfigFactory.parseString(plain), defaults).withFallback(defaults);
            Settings.config = config.getConfig("opencomputers");
            settings = new Settings();
        } catch (Throwable e) {
            if (file.exists()) {
                throw new RuntimeException("Error parsing configuration file. To restore defaults, delete '" + file.getName() + "' and restart the game.", e);
            }
            Settings.config = defaults.getConfig("opencomputers");
            settings = new Settings();
            config = defaults;
        }

        for (String key : forbiddenConfigLists) {
            if (config.hasPath(prefix + key) && !config.getStringList(prefix + key).isEmpty()) {
                throw new RuntimeException("Error parsing configuration file: removed configuration option '" + key + "' is not empty. This option should no longer be used.");
            }
        }

        saveConfig(file, config);
    }

    private static Config loadDefaults() {
        try (
                InputStream in = Settings.class.getResourceAsStream("/application.conf");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
        ) {
            String configStr = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return ConfigFactory.parseString(configStr);
        } catch (IOException e) {
            throw new RuntimeException("Error loading default configuration.", e);
        }
    }

    private static void saveConfig(File file, Config config) {
        ConfigRenderOptions renderOptions = ConfigRenderOptions.defaults().setJson(false).setOriginComments(false);
        String nl = System.getProperty("line.separator");

        try {
            String renderedConfig = config.root().render(renderOptions);
            Pattern pattern = Pattern.compile("^(\\s*)", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(renderedConfig);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).replace("  ", " "));
            }
            matcher.appendTail(sb);

            String indentedConfig = sb.toString();
            String finalConfig = indentedConfig.replaceAll("((?:\\s*#.*" + nl + ")(?:\\s*[^#\\s].*" + nl + ")+)", "$1" + nl);

            Files.createDirectories(file.toPath().getParent());
            try (PrintWriter out = new PrintWriter(file)) {
                out.write(finalConfig);
            }
        } catch (IOException e) {
            OpenComputers.log().warn("Failed saving config.", e);
        }
    }

    private static VersionRange createFromVersionSpec(String spec) {
        try {
            return VersionRange.createFromVersionSpec(spec);
        } catch (InvalidVersionSpecificationException e) {
            // Born to write perfect version strings, forced to write try/catch around code that will never throw.
            throw new RuntimeException(e);
        }
    }

    // Usage: add(new AbstractMap.SimpleEntry<>(createFromVersionSpec("[0.0,1.5)"), new String[]{"computer.ramSizes"} ))
    // will re-set the value of `computer.ramSizes` if a config saved with a version < 1.5 is loaded.
    private static final List<Map.Entry<VersionRange, String[]>> configPatches = new ArrayList<Map.Entry<VersionRange, String[]>>() {{
        add(new AbstractMap.SimpleEntry<>(
                createFromVersionSpec("[0.0, 1.5.20)"),
                new String[]{"switch.relayDelayUpgrade"}
        ));
        add(new AbstractMap.SimpleEntry<>(
                createFromVersionSpec("[0.0, 1.6.2)"),
                new String[]{"nanomachines.potionWhitelist"}
        ));
        add(new AbstractMap.SimpleEntry<>(
                createFromVersionSpec("[0.0, 1.7.2)"),
                new String[]{
                        "power.cost.wirelessCostPerRange",
                        "misc.maxWirelessRange",
                        "misc.maxOpenPorts",
                        "computer.cpuComponentCount"
                }
        ));
        add(new AbstractMap.SimpleEntry<>(
                createFromVersionSpec("[0.0, 1.8.0)"),
                new String[]{"computer.robot.limitFlightHeight"}
        ));
    }};
    private static final VersionRange fileringRulesPatchVersion = createFromVersionSpec("[0.0, 1.8.3)");

    // Checks the config version (i.e. the version of the mod the config was
    // created by) against the current version to see if some hard changes
    // were made. If so, the new default values are copied over.
    private static Config patchConfig(Config config, Config defaults) {
        DefaultArtifactVersion modVersion = new DefaultArtifactVersion(OpenComputers$.MODULE$.Version());
        DefaultArtifactVersion configVersion = new DefaultArtifactVersion(config.hasPath(prefix + "version") ? config.getString(prefix + "version") : "0.0.0");

        Config patched = config;
        if (!configVersion.equals(modVersion)) {
            OpenComputers.log().info(String.format("Updating config from version '%s' to '%s'.",
                    configVersion, defaults.getString(prefix + "version")));
            patched = patched.withValue(prefix + "version", defaults.getValue(prefix + "version"));

            for (Map.Entry<VersionRange, String[]> entry : configPatches) {
                VersionRange version = entry.getKey();
                String[] paths = entry.getValue();

                if (version.containsVersion(configVersion)) {
                    for (String path : paths) {
                        String fullPath = prefix + path;
                        OpenComputers.log().info("=> Updating setting '" + fullPath + "'. ");
                        if (defaults.hasPath(fullPath)) {
                            patched = patched.withValue(fullPath, defaults.getValue(fullPath));
                        } else {
                            patched = patched.withoutPath(fullPath);
                        }
                    }
                }
            }

            // Migrate filtering rules to 1.8.3+
            if (fileringRulesPatchVersion.containsVersion(configVersion) && patched.hasPath(prefix + "internet.whitelist") && patched.hasPath(prefix + "internet.blacklist")) {
                OpenComputers.log().info("=> Migrating Internet Card filtering rules. ");
                Pattern cidrPattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(?:/(\\d{1,2}))");
                List<String> httpHostWhitelist = patched.getStringList(prefix + "internet.whitelist");
                List<String> httpHostBlacklist = patched.getStringList(prefix + "internet.blacklist");
                ArrayList<String> internetFilteringRules = new ArrayList<>();
                for (String blockedAddress: httpHostBlacklist) {
                    if (cidrPattern.matcher(blockedAddress).find()) {
                        internetFilteringRules.add("deny ip:" + blockedAddress);
                    } else {
                        internetFilteringRules.add("deny domain:" + blockedAddress);
                    }
                }
                for (String allowedAddress: httpHostWhitelist) {
                    if (cidrPattern.matcher(allowedAddress).find()) {
                        internetFilteringRules.add("allow ip:" + allowedAddress);
                    } else {
                        internetFilteringRules.add("allow domain:" + allowedAddress);
                    }
                }
                if (httpHostWhitelist.isEmpty()) {
                    internetFilteringRules.add("deny all");
                }
                internetFilteringRules.addAll(defaults.getStringList(prefix + "internet.filteringRules"));
                ConfigValue patchedRules = ConfigValueFactory.fromIterable(internetFilteringRules);
                // We need to use the private APIs here, unfortunately.
                try {
                    for (String key: Arrays.asList("internet.whitelist", "internet.blacklist")) {
                        if (patched.hasPath(prefix + key)) {
                            ConfigValue originalValue = patched.getValue(prefix + key);
                            ConfigValue deprecatedValue = ConfigValueFactory.fromIterable(new ArrayList<String>(), originalValue.origin().description());
                            List<String> comments = Arrays.asList("No longer used! See internet.filteringRules.", "", "Previous contents:");
                            for (String value: patched.getStringList(prefix + key)) {
                                comments.add("\"" + value + "\"");
                            }
                            deprecatedValue = OpenComputersConfigCommentManipulationHook.setComments(deprecatedValue, comments);
                            patched = patched.withValue(prefix + key, deprecatedValue);
                        }
                    }
                    patchedRules = OpenComputersConfigCommentManipulationHook.setComments(
                            patchedRules, defaults.getValue(prefix + "internet.filteringRules").origin().comments()
                    );
                } catch (Throwable e) {
                    /* pass */
                }
                patched = patched.withValue(prefix + "internet.filteringRules", patchedRules);
            }
        }
        return patched;
    }

    public static abstract class DebugCardAccess {
        public abstract Optional<String> checkAccess(Optional<DebugCard.AccessContext> ctxOpt);

        public static class Forbidden extends DebugCardAccess {
            @Override
            public Optional<String> checkAccess(Optional<DebugCard.AccessContext> ctxOpt) {
                return Optional.of("debug card is disabled");
            }
        }

        public static class Allowed extends DebugCardAccess {
            @Override
            public Optional<String> checkAccess(Optional<DebugCard.AccessContext> ctxOpt) {
                return Optional.empty();
            }
        }

        public static class Whitelist extends DebugCardAccess {
            private final File noncesFile;
            private final Map<String, String> values = new HashMap<>();
            private final SecureRandom rng;

            public Whitelist(File noncesFile) {
                this.noncesFile = noncesFile;
                try {
                    rng = SecureRandom.getInstance("SHA1PRNG");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                load();
            }

            public void save() {
                File noncesDir = noncesFile.getParentFile();
                try {
                    if (!noncesDir.exists() && !noncesDir.mkdirs())
                        throw new RuntimeException("Cannot create nonces directory: " + noncesDir.getCanonicalPath());

                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(noncesFile.toPath()), StandardCharsets.UTF_8), false)) {
                        for (Map.Entry<String, String> entry : values.entrySet())
                            writer.println(entry.getKey() + " " + entry.getValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public void load() {
                values.clear();

                if (!noncesFile.exists())
                    return;

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(noncesFile.toPath()), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ", 2);
                        if (parts.length == 2) {
                            values.put(parts[0], parts[1]);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            private String generateNonce() {
                byte[] buf = new byte[16];
                rng.nextBytes(buf);
                return new String(Hex.encodeHex(buf, true));
            }

            public Optional<String> nonce(String player) {
                return Optional.ofNullable(values.get(player.toLowerCase()));
            }

            public boolean isWhitelisted(String player) {
                return values.containsKey(player.toLowerCase());
            }

            public Set<String> whitelist() {
                return Collections.unmodifiableSet(values.keySet());
            }

            public void add(String player) {
                if (!values.containsKey(player.toLowerCase())) {
                    values.put(player.toLowerCase(), generateNonce());
                    save();
                }
            }

            public void remove(String player) {
                if (values.remove(player.toLowerCase()) != null) {
                    save();
                }
            }

            public void invalidate(String player) {
                if (values.containsKey(player.toLowerCase())) {
                    values.put(player.toLowerCase(), generateNonce());
                    save();
                }
            }

            @Override
            public Optional<String> checkAccess(Optional<DebugCard.AccessContext> ctxOpt) {
                if (!ctxOpt.isPresent()) {
                    return Optional.of("debug card is whitelisted, Shift+Click with it to bind card to yourself");
                }

                DebugCard.AccessContext ctx = ctxOpt.get();
                String nonce = values.get(ctx.player().toLowerCase());

                if (nonce == null) {
                    return Optional.of("you are not whitelisted to use debug card");
                } else if (nonce.equals(ctx.nonce())) {
                    return Optional.empty();
                } else {
                    return Optional.of("debug card is invalidated, please re-bind it to yourself");
                }
            }
        }
    }


    public static List<Integer> getIntList(Config config, String path, Optional<List<Integer>> def) {
        if (config.hasPath(path))
            return config.getIntList(path);
        else
            return def.orElseGet(LinkedList::new);
    }
}
