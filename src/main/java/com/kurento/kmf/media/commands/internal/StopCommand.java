package com.kurento.kmf.media.commands.internal;

import com.kurento.kmf.media.internal.ProvidesMediaCommand;
import com.kurento.kms.thrift.api.mediaCommandDataTypesConstants;

@ProvidesMediaCommand(type = mediaCommandDataTypesConstants.STOP)
public class StopCommand extends VoidCommand {

	public StopCommand() {
		super(mediaCommandDataTypesConstants.STOP);
	}

}
