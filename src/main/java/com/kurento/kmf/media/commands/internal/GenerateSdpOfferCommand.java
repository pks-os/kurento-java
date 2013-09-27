package com.kurento.kmf.media.commands.internal;

import com.kurento.kmf.media.internal.ProvidesMediaCommand;
import com.kurento.kms.thrift.api.mediaCommandDataTypesConstants;

@ProvidesMediaCommand(type = mediaCommandDataTypesConstants.GENERATE_SDP_OFFER)
public class GenerateSdpOfferCommand extends VoidCommand {

	public GenerateSdpOfferCommand() {
		super(mediaCommandDataTypesConstants.GENERATE_SDP_OFFER);
	}

}
