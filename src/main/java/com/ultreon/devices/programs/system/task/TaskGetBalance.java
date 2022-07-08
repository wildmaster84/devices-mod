package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TaskGetBalance extends Task {
    private int balance;

    public TaskGetBalance() {
        super("bank_get_balance");
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
    }

    @Override
    public void processRequest(CompoundTag nbt, Level level, Player player) {
        Account account = BankUtil.INSTANCE.getAccount(player);
        this.balance = account.getBalance();
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
        nbt.putInt("balance", this.balance);
    }

    @Override
    public void processResponse(CompoundTag nbt) {
    }
}
