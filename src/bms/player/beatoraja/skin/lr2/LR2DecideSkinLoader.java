package bms.player.beatoraja.skin.lr2;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import bms.player.beatoraja.decide.MusicDecide;
import bms.player.beatoraja.decide.MusicDecideSkin;
import bms.player.beatoraja.skin.SkinHeader;

public class LR2DecideSkinLoader extends LR2PlaySkinLoader {

	public LR2DecideSkinLoader(float srcw, float srch, float dstw, float dsth) {
		super(srcw, srch, dstw, dsth);
	}

	private MusicDecideSkin skin;
	
	public MusicDecideSkin loadMusicDecideSkin(File f, MusicDecide decide, SkinHeader header, Map<Integer, Boolean> option, Map property) throws IOException {

		skin = new MusicDecideSkin(srcw, srch, dstw, dsth);

		this.loadSkin(skin, f, decide, header, option, property);

		return skin;
	}
}
