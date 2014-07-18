package codeeer.parkssword_android.app;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5
{
    static int[] RandNum = {
            12,34,56,78,23,45,67,89,13,24,
            35,46,57,68,79,80,14,25,36,47,
            58,69,70,98,87,76,65,54,43,32,
            11,22,33,44,55,66,77,88,99,0,
            17,28,39,40,51,62,73,84,95,7,
            74,83,48,96,36,26,96,25,85,1,
            97,43,98,37,96,13,63,74,84,51,
            85,25,96,24,87,24,98,23,64,73,
            18, 2,48,93,93,17,39,45,9,2,
            95,24,12,16,19,24,84,45,29,20,
    };


    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    public static String toHexString(byte[] b) {  //String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
        return sb.toString();
    }

    public static String Bit32(String SourceString) throws Exception {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(SourceString.getBytes());
        byte messageDigest[] = digest.digest();
        return toHexString(messageDigest);
    }

    public static String Bit16(String SourceString) throws Exception {
        return Bit32(SourceString).substring(8, 24);
    }

    public static String Manual(String SourceString, String PWDsPool, int Length, int MD5Times, String CPUID, String HardID, String USBID) throws Exception {

         //乱序
        PWDsPool = FlyTheWords(PWDsPool);

        StringBuilder CreatStr = new StringBuilder("");

        //把序列号参数进行加密运算
        String MD5Temp = Bit16(SourceString + CPUID);
        MD5Temp = Bit16(MD5Temp + HardID);
        MD5Temp = Bit16(MD5Temp + USBID);

        for (int i = 0; i < MD5Times; i++)
        {
            MD5Temp = Bit16(MD5Temp);
        }

        int LastGet = -1;//记录上一次提取的，比对，避免重复
        int PlusTwoEveryTime = 1;//每次加2，用以打乱生成

        for (int i = 0; i < Length + 16; )
        {
            MD5Temp = Bit16(MD5Temp);
            PlusTwoEveryTime += 2;

            int FinalNum = HasNumber(MD5Temp, PlusTwoEveryTime);

            if (FinalNum != -1 && LastGet != Character.getNumericValue(MD5Temp.charAt(FinalNum)))
            {
                CreatStr.append(PWDsPool.charAt(Character.getNumericValue(MD5Temp.charAt(FinalNum))));

                LastGet = Character.getNumericValue(MD5Temp.charAt(FinalNum));
                i++;
            }
        }

        StringBuilder ReadyStr = new StringBuilder();

        if (HasNumber(CreatStr.toString(), 0) != -1) ReadyStr.append(PWDsPool.charAt(HasNumber(PWDsPool, 0)));
        if (HasLetter(CreatStr.toString()) != -1) ReadyStr.append(PWDsPool.charAt(HasLetter(PWDsPool)));
        if (HasCharacter(CreatStr.toString()) != -1) ReadyStr.append(PWDsPool.charAt(HasCharacter(PWDsPool)));

        ReadyStr.append(CreatStr.toString());
        return ReadyStr.toString().substring(0, Length);
    }

    /// <summary>
    /// 乱序算法
    /// </summary>
    /// <param name="PWDs">原字符串</param>
    /// <returns>打乱后的库</returns>
    static String FlyTheWords(String PWDs)
    {
        StringBuilder FinalStr = new StringBuilder();
        for (int i = 0; i < (160 / PWDs.length() + 1); i++)
        {
            FinalStr.append(PWDs);
        }

        String Temp = FinalStr.toString().substring(50,100+50);

        StringBuilder SndTemp = new StringBuilder();
        for (int aRandNum : RandNum) {
            SndTemp.append(Temp.charAt(aRandNum));
        }

        return SndTemp.toString();
    }

    /// <summary>
    /// 判定字符串中是否包含数字
    /// </summary>
    /// <param name="Source">字符串</param>
    /// <param name="FirstIndex">搜索的起始位置</param>
    /// <returns>数字的位置</returns>
    static int HasNumber(String Source, int FirstIndex)
    {
        int Has = -1;

        if (FirstIndex > Source.length())
        {
            FirstIndex = FirstIndex - (FirstIndex / Source.length()) * Source.length();
        }

        for (int i = FirstIndex; i < Source.length(); i++)
        {
            if (Character.isDigit(Source.charAt(i)))
            {
                Has = i;
                break;
            }
        }

        return Has;
    }

    static int HasLetter(String Source)
    {
        int Has = -1;
        for (int i = 0; i < Source.length(); i++)
        {
            if (Character.isLetter(Source.charAt(i)))
            {
                Has = i;
                break;
            }
        }
        return Has;
    }

    static int HasCharacter(String Source)
    {
        int Has = -1;
        for (int i = 0; i < Source.length(); i++)
        {
            if (Character.isLetterOrDigit(Source.charAt(i)))
            {
                continue;
            }
            Has = i;
            break;
        }
        return Has;
    }
}
