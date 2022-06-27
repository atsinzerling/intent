public class Methods {
    public static String wrap(String orig){

        char[] charArr = (orig+" ").toCharArray();

        int lastLineBreak = 0;
        int lastSpace = 0;
        for (int i = 0; i < charArr.length; i++){
            if (charArr[i] == ' '){
                if (i-lastLineBreak >=60){
                    charArr[lastSpace] = '\n';
                    lastLineBreak = lastSpace;
                }
                lastSpace = i;
            }
        }
        return new String(charArr).substring(0, charArr.length-1);
    }
}
