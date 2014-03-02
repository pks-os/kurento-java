package com.kurento.kmf.media;

import com.kurento.tool.rom.RemoteClass;
import com.kurento.tool.rom.server.Param;

@RemoteClass
public interface JackVaderFilter extends Filter {

	public interface Factory {

		public Builder create(
				@Param("mediaPipeline") MediaPipeline mediaPipeline);
	}

	public interface Builder extends AbstractBuilder<JackVaderFilter> {

		public Builder withGarbagePeriod(int garbagePeriod);
	}
}
