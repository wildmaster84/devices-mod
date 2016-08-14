package com.mrcrayfish.device.programs.email.tasks;

import com.mrcrayfish.device.programs.email.ApplicationEmail.Email;
import com.mrcrayfish.device.programs.email.ApplicationEmail.EmailManager;
import com.mrcrayfish.device.task.Task;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TaskSendEmail extends Task 
{
	private Email email;
	private String to;
	
	public TaskSendEmail() 
	{
		super("send_email");
	}
	
	public TaskSendEmail(Email email, String to)
	{
		this();
		this.email = email;
		this.to = to;
	}

	@Override
	public void prepareRequest(NBTTagCompound nbt) 
	{
		this.email.writeToNBT(nbt);
		nbt.setString("to", this.to);
	}

	@Override
	public void processRequest(NBTTagCompound nbt, World world, EntityPlayer player) 
	{
		String name = EmailManager.INSTANCE.getName(player);
		if(name != null)
		{
			Email email = Email.readFromNBT(nbt);
			email.setAuthor(name);
			if(EmailManager.INSTANCE.addEmailToInbox(email, nbt.getString("to"))) 
			{
				this.setSuccessful();
			}
		}
	}

	@Override
	public void prepareResponse(NBTTagCompound nbt) 
	{
		nbt.setBoolean("Sent", this.isSucessful());
	}

	@Override
	public void processResponse(NBTTagCompound nbt) 
	{
		if(nbt.getBoolean("Sent")) 
			this.setSuccessful();
	}

}