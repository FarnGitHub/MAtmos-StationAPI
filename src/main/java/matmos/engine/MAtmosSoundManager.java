package matmos.engine;

public interface MAtmosSoundManager {
	void routine();

	void cacheSound(String var1);

	void playSound(String var1, float var2, float var3, int var4);

	int getNewStreamingToken();

	void setupStreamingToken(int var1, String var2, float var3, float var4);

	void startStreaming(int var1, float var2, int var3);

	void stopStreaming(int var1, float var2);

	void pauseStreaming(int var1, float var2);

	void eraseStreamingToken(int var1);
}
