package bms.player.beatoraja.skin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import bms.player.beatoraja.PlaySkin;

public abstract class LR2SkinLoader {

	private List<CommandWord> commands = new ArrayList();

	List<Texture> imagelist = new ArrayList();

	protected void addCommandWord(CommandWord cm) {
		commands.add(cm);
	}

	protected void loadSkin(Skin skin, File f) throws IOException {
		this.loadSkin(skin, f, new int[0]);
	}

	protected void loadSkin(Skin skin, File f, int[] option) throws IOException {
		float srcw = 640;
		float srch = 480;
		float dstw = 1280;
		float dsth = 720;

		List<SkinObject> partlist = new ArrayList();
		SkinObject part = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "MS932"));
		String line = null;

		boolean skip = false;
		boolean ifs = false;

		while ((line = br.readLine()) != null) {
			if (!line.startsWith("//")) {
				String[] str = line.split(",", -1);
				if (str.length > 0) {
					if (str[0].equals("#IF")) {
						for (int i = 1; i < str.length; i++) {
							try {
								int opt = Integer.parseInt(str[i]);
								for (int j = 0; j < option.length; j++) {
									if (option[j] == opt) {
										ifs = true;
									}
								}
							} catch (NumberFormatException e) {
								break;
							}
						}

						skip = !ifs;
					}
					if (str[0].equals("#ELSEIF")) {
						if (ifs) {
							skip = true;
						} else {
							for (int i = 1; i < str.length; i++) {
								try {
									int opt = Integer.parseInt(str[i]);
									for (int j = 0; j < option.length; j++) {
										if (option[j] == opt) {
											ifs = true;
										}
									}
								} catch (NumberFormatException e) {
									break;
								}
							}

							skip = !ifs;
						}
					}
					if (str[0].equals("#ELSE")) {
						skip = ifs;
					}
					if (str[0].equals("#ENDIF")) {
						skip = false;
						ifs = false;
					}
					if (!skip) {

						if (str[0].equals("#IMAGE")) {
							String imagepath = str[1].replace("LR2files\\Theme", "skin").replace("\\", "/");
							File imagefile = new File(imagepath);
							if (imagepath.contains("*")) {
								String ext = imagepath.substring(imagepath.lastIndexOf("*") + 1);
								File imagedir = new File(imagepath.substring(0, imagepath.lastIndexOf('/')));
								if (imagedir.exists() && imagedir.isDirectory()) {
									for (File subfile : imagedir.listFiles()) {
										if (subfile.getPath().toLowerCase().endsWith(ext)) {
											imagefile = subfile;
											break;
										}
									}
								}
							}
							if (imagefile.exists()) {
								try {
									imagelist.add(new Texture(Gdx.files.internal(imagefile.getPath())));
								} catch (GdxRuntimeException e) {
									imagelist.add(null);
									e.printStackTrace();
								}
							} else {
								imagelist.add(null);
							}
							System.out
									.println("Image Loaded - " + (imagelist.size() - 1) + " : " + imagefile.getPath());
						}

						if (str[0].equals("#SRC_IMAGE")) {
							int gr = Integer.parseInt(str[2]);
							if (gr < imagelist.size() && imagelist.get(gr) != null) {
								try {
									int[] values = parseInt(str);
									int x = values[3];
									int y = Integer.parseInt(str[4]);
									int w = Integer.parseInt(str[5]);
									if(w == -1) {
										w = imagelist.get(gr).getWidth();
									}
									int h = Integer.parseInt(str[6]);
									if(h == -1) {
										h = imagelist.get(gr).getHeight();
									}
									int divx = Integer.parseInt(str[7]);
									if(divx <= 0) {
										divx = 1;
									}
									int divy = Integer.parseInt(str[8]);
									if(divy <= 0) {
										divy = 1;
									}
									TextureRegion[] images = new TextureRegion[divx * divy];
									for (int i = 0; i < divx; i++) {
										for (int j = 0; j < divy; j++) {
											images[divx * j + i] = new TextureRegion(imagelist.get(gr), x + w / divx
													* i, y + h / divy * j, w / divx, h / divy);
										}
									}
									part = new SkinObject();
									part.setImage(images, values[9]);
									part.setTiming(values[10]);
									partlist.add(part);
								} catch (NumberFormatException e) {
									e.printStackTrace();
								}
							}
						}
						if (str[0].equals("#DST_IMAGE")) {
							if (part != null) {
								try {
									int[] values = parseInt(str);
									part.setDestination(values[2], values[3] * dstw
											/ srcw, dsth - (values[4] + values[6]) * dsth / srch, values[5]
											* dstw / srcw, values[6] * dsth / srch,
											values[7], values[8],
											values[9], values[10],
											values[11], values[12],
											values[13], values[14],values[15],values[16],values[17],values[18],values[19],values[20]);										
								} catch (NumberFormatException e) {
									e.printStackTrace();
								}
							}
						}

						for (CommandWord cm : commands) {
							if (str[0].equals("#" + cm.str)) {
								cm.execute(str);
							}
						}
					}

					// if (str[0].equals("#DST_SLIDER")) {
					// slider = new Sprite(imagelist.get(Integer
					// .parseInt(str[2])),
					// Integer.parseInt(str[3]),
					// Integer.parseInt(str[4]),
					// Integer.parseInt(str[5]),
					// Integer.parseInt(str[6]));
					// }
				}
			}
		}

		skin.setSkinPart(partlist.toArray(new SkinObject[0]));

	}
	
	private int[] parseInt(String[] s) {
		int[] result = new int[21];
		for(int i = 2;i < s.length;i++) {
			try {
				result[i] =  Integer.parseInt(s[i]);
			} catch(Exception e) {
				
			}
		}
		return result;
	}

	public abstract class CommandWord {

		public final String str;

		public CommandWord(String str) {
			this.str = str;
		}

		public abstract void execute(String[] values);
	}
}
