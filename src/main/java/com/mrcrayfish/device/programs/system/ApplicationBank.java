package com.mrcrayfish.device.programs.system;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.BankUtil;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.programs.system.task.TaskDeposit;
import com.mrcrayfish.device.programs.system.task.TaskWithdraw;
import com.mrcrayfish.device.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.awt.*;

@SuppressWarnings("FieldCanBeLocal")
public class ApplicationBank extends SystemApplication {
    private static final ItemStack EMERALD = new ItemStack(Items.EMERALD);
    private static final ResourceLocation BANK_ASSETS = new ResourceLocation("cdm:textures/gui/bank.png");
    //    private static final ResourceLocation villagerTextures = new ResourceLocation("textures/entity/villager/villager.png");
//    private static final VillagerModel<Villager> villagerModel = new VillagerModel<Villager>();
    private Villager villager;
    private Layout layoutStart;
    private Label labelTeller;
    private Text textWelcome;
    private Button btnDepositWithdraw;
    private Button btnTransfer;
    private Layout layoutMain;
    private Label labelBalance;
    private Label labelAmount;
    private TextField amountField;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    private Button btnSeven;
    private Button btnEight;
    private Button btnNine;
    private Button btnZero;
    private Button btnClear;
    private Button buttonDeposit;
    private Button buttonWithdraw;
    private Label labelEmeraldAmount;
    private Label labelInventory;
    private int emeraldAmount;
    private int rotation;

    {
    }

    public ApplicationBank() {
        //super(Reference.MOD_ID + "Bank", "The Emerald Bank");
    }

    @Override
    public void onTick() {
        super.onTick();
        rotation++;
        if (rotation >= 100) {
            rotation = 0;
        }
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        layoutStart = new Layout();
        layoutStart.setBackground((pose, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            assert Minecraft.getInstance().level != null;
            villager = new Villager(EntityType.VILLAGER, Minecraft.getInstance().level, VillagerType.PLAINS);

            pose.pushPose();
            {
                RenderSystem.enableDepthTest();
                pose.translate(x + 25, y + 33, 15);
                pose.scale((float) -2.5, (float) -2.5, (float) -2.5);
                // Todo: do rotations
                pose.mulPose(new Quaternion(1, 0, 0, -10F));
                pose.mulPose(new Quaternion(0, 0, 1, 180F));
                pose.mulPose(new Quaternion(0, 1, 0, -20F));
                float scaleX = (mouseX - x - 25) / (float) width;
                float scaleY = (mouseY - y - 20) / (float) height;
//                RenderSystem.setShaderTexture(villagerTextures);
                MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                Minecraft.getInstance().getEntityRenderDispatcher().render(villager, 0f, 0f, 0f, 0f, 0f, pose, buffer, 1);
                RenderSystem.disableDepthTest();
            }
            pose.popPose();

            RenderSystem.setShaderTexture(0, BANK_ASSETS);
            RenderUtil.drawRectWithTexture(pose, x + 46, y + 19, 0, 0, 146, 52, 146, 52);
        });

        labelTeller = new Label(ChatFormatting.YELLOW + "Casey The Teller", 60, 7);
        layoutStart.addComponent(labelTeller);

        assert Minecraft.getInstance().player != null;
        textWelcome = new Text(ChatFormatting.BLACK + "Hello " + Minecraft.getInstance().player.getName() + ", welcome to The Emerald Bank! How can I help you?", 62, 25, 125);
        layoutStart.addComponent(textWelcome);

        btnDepositWithdraw = new Button(54, 74, "View Account");
        btnDepositWithdraw.setSize(76, 20);
        btnDepositWithdraw.setToolTip("View Account", "Shows your balance");
        layoutStart.addComponent(btnDepositWithdraw);

        btnTransfer = new Button(133, 74, "Transfer");
        btnTransfer.setSize(58, 20);
        btnTransfer.setToolTip("Transfer", "Withdraw and deposit emeralds");
        btnTransfer.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                setCurrentLayout(layoutMain);
            }
        });
        layoutStart.addComponent(btnTransfer);

        setCurrentLayout(layoutStart);

        layoutMain = new Layout(120, 143) {
            @Override
            public void handleTick() {
                super.handleTick();
                int amount = InventoryUtil.getItemAmount(Minecraft.getInstance().player, Items.EMERALD);
                labelEmeraldAmount.setText("x " + amount);
            }
        };
        layoutMain.setBackground((pose, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) ->
        {
            Gui.fill(pose, x, y, x + width, y + 40, Color.GRAY.getRGB());
            Gui.fill(pose, x, y + 39, x + width, y + 40, Color.DARK_GRAY.getRGB());
            Gui.fill(pose, x + 62, y + 103, x + 115, y + 138, Color.BLACK.getRGB());
            Gui.fill(pose, x + 63, y + 104, x + 114, y + 113, Color.DARK_GRAY.getRGB());
            Gui.fill(pose, x + 63, y + 114, x + 114, y + 137, Color.GRAY.getRGB());
            RenderUtil.renderItem(x + 65, y + 118, EMERALD, false);
        });

        labelBalance = new Label("Balance", 60, 5);
        labelBalance.setAlignment(Label.ALIGN_CENTER);
        labelBalance.setShadow(false);
        layoutMain.addComponent(labelBalance);

        labelAmount = new Label("Loading balance...", 60, 18);
        labelAmount.setAlignment(Label.ALIGN_CENTER);
        labelAmount.setScale(2);
        layoutMain.addComponent(labelAmount);

        amountField = new TextField(5, 45, 110);
        amountField.setText("0");
        amountField.setEditable(false);
        layoutMain.addComponent(amountField);

        for (int i = 0; i < 9; i++) {
            int posX = 5 + (i % 3) * 19;
            int posY = 65 + (i / 3) * 19;
            Button button = new Button(posX, posY, Integer.toString(i + 1));
            button.setSize(16, 16);
            addNumberClickListener(button, amountField, i + 1);
            layoutMain.addComponent(button);
        }

        btnZero = new Button(5, 122, "0");
        btnZero.setSize(16, 16);
        addNumberClickListener(btnZero, amountField, 0);
        layoutMain.addComponent(btnZero);

        btnClear = new Button(24, 122, "Clr");
        btnClear.setSize(35, 16);
        btnClear.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                amountField.setText("0");
            }
        });
        layoutMain.addComponent(btnClear);

        buttonDeposit = new Button(62, 65, "Deposit");
        buttonDeposit.setSize(53, 16);
        buttonDeposit.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (amountField.getText().equals("0")) {
                    return;
                }

                try {
                    final int amount = Integer.parseInt(amountField.getText());
                    deposit(amount, (nbt, success) ->
                    {
                        if (success) {
                            assert nbt != null;
                            int balance = nbt.getInt("balance");
                            labelAmount.setText("$" + balance);
                            amountField.setText("0");
                        }
                    });
                } catch (NumberFormatException e) {
                    amountField.setText("0");
                    openDialog(new Dialog.Message("Invalid amount. The maximum that you can deposit is " + Integer.MAX_VALUE));
                }
            }
        });
        layoutMain.addComponent(buttonDeposit);

        buttonWithdraw = new Button(62, 84, "Withdraw");
        buttonWithdraw.setSize(53, 16);
        buttonWithdraw.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (amountField.getText().equals("0")) {
                    return;
                }

                try {
                    final int amount = Integer.parseInt(amountField.getText());
                    withdraw(amount, (nbt, success) ->
                    {
                        if (success) {
                            assert nbt != null;
                            int balance = nbt.getInt("balance");
                            labelAmount.setText("$" + balance);
                            amountField.setText("0");
                        }
                    });
                } catch (NumberFormatException e) {
                    amountField.setText("0");
                    openDialog(new Dialog.Message("Invalid amount. The maximum that you can withdraw is " + Integer.MAX_VALUE));
                }
            }
        });
        layoutMain.addComponent(buttonWithdraw);

        labelEmeraldAmount = new Label("x 0", 83, 123);
        layoutMain.addComponent(labelEmeraldAmount);

        labelInventory = new Label("Wallet", 74, 105);
        labelInventory.setShadow(false);
        layoutMain.addComponent(labelInventory);

        BankUtil.getBalance((nbt, success) ->
        {
            if (success) {
                assert nbt != null;
                int balance = nbt.getInt("balance");
                labelAmount.setText("$" + balance);
            }
        });
    }

    public void addNumberClickListener(Button btn, final TextField field, final int number) {
        btn.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (!(field.getText().equals("0") && number == 0)) {
                    if (field.getText().equals("0"))
                        field.clear();
                    field.writeText(Integer.toString(number));
                }
            }
        });
    }

    private void deposit(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskDeposit(amount).setCallback(callback));
    }

    private void withdraw(int amount, Callback<CompoundTag> callback) {
        TaskManager.sendTask(new TaskWithdraw(amount).setCallback(callback));
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }
}
