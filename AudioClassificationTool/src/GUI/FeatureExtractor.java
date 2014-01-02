package GUI;

public class FeatureExtractor {
 private float sampleRate;
 private double timeSpan;
 
 private Frame[] frameArray;
 
 public FeatureExtractor(int[] pAudioData, double seconds, float samplerate){
	 this.timeSpan = seconds;
	 this.sampleRate = samplerate;
	 
	 frameArray = new Frame[(int) timeSpan];
	 for(int i = 0; i < timeSpan; i++)
	 {
		int[] tempArray = new int[(int)sampleRate];
		System.arraycopy(pAudioData, (int)(i*sampleRate), tempArray, 0, (int)sampleRate);
		frameArray[i] =  new Frame(tempArray, i);
	 }
 }

public void prepare(){
	for (int i = 0; i < frameArray.length; i++){
		frameArray[i].setHZCRR(calculateHZCRR(frameArray[i]));
		frameArray[i].setSF(calculateSF(frameArray[i]));
		frameArray[i].setLSTER(calculateLSTER(frameArray[i]));
		frameArray[i].setBF(calculateBF(frameArray[i]));
		frameArray[i].setSTE(calculateSTE(frameArray[i]));
		frameArray[i].setNFR(calculateNFR(frameArray[i]));
		
	}
}

private int calculateHZCRR(Frame f){
    int zcr = 0;
    for(int i = 1; i < sampleRate; i++){
        if (Math.signum(f.getAudioData()[i]) != Math.signum(f.getAudioData()[i-1]))
            zcr ++;
    }
    return zcr;
}

public int calculateSF(Frame f1){
	int sf =0;
	Frame f2 =null;
	if(f1.getPosition()+1< frameArray.length){
		f2 = frameArray[f1.getPosition()];
	}
	return sf;
}
public int calculateLSTER(Frame f){
	return 0;
	
}
public int calculateBF(Frame f){
	return 0;
	
}
public	int calculateSTE(Frame f){
	return 0;
	
}
public int calculateNFR(Frame f){
	return 0;
	
}

}
