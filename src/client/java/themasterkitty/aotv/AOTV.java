package themasterkitty.aotv;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AOTV implements ClientModInitializer {
	public static double dist = 5;
	private final File data = new File("aotv.txt");
	@Override
	public void onInitializeClient() {
		if (data.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(data))) {
				StringBuilder builder = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				dist = Double.parseDouble(builder.toString());
			} catch (IOException ignored) { }
		}
		else {
			try {
				data.createNewFile();
				FileWriter writer = new FileWriter(data);
				writer.write("5.0");
				writer.close();
			} catch (IOException ignored) { }
		}

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, ignored) -> dispatcher.register(literal("aotv")
				.then(argument("dist", DoubleArgumentType.doubleArg(0))
						.executes(ctx -> {
							dist = DoubleArgumentType.getDouble(ctx, "dist");
							assert MinecraftClient.getInstance().player != null;
							MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bAOTV Distance set to " + dist + "m/tp"));
							try (FileWriter writer = new FileWriter(data)) {
								writer.write(String.valueOf(dist));
							} catch (IOException ignored1) { }

							return 1;
						})
				).executes(ctx -> {
					assert MinecraftClient.getInstance().player != null;
					MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§bAOTV Distance is " + dist + "m/tp. Set to 0 to disable."));

					return 1;
				})));
	}
}