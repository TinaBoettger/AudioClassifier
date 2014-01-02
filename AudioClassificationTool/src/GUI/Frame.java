package GUI;

public class Frame {
	private int HZCRR;
	private int SF;
	private int LSTER;
	private int BF;
	private	int STE;
	private int NFR;
	private int[] audioData;
	private int position;
	
	public Frame(int[] audioData, int position) {
		this.setAudioData(audioData);
		this.position = position;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getHZCRR() {
		return HZCRR;
	}
	public void setHZCRR(int hZCRR) {
		HZCRR = hZCRR;
	}
	public int getSF() {
		return SF;
	}
	public void setSF(int sF) {
		SF = sF;
	}
	public int getLSTER() {
		return LSTER;
	}
	public void setLSTER(int lSTER) {
		LSTER = lSTER;
	}
	public int getBF() {
		return BF;
	}
	public void setBF(int bF) {
		BF = bF;
	}
	public int getSTE() {
		return STE;
	}
	public void setSTE(int sTE) {
		STE = sTE;
	}
	public int getNFR() {
		return NFR;
	}
	public void setNFR(int nFR) {
		NFR = nFR;
	}
	public int[] getAudioData() {
		return audioData;
	}
	public void setAudioData(int[] audioData) {
		this.audioData = audioData;
	}

}
