import java.nio.ByteBuffer;
import java.util.Arrays;

public class T {
  public static void main(String[] args) {
//    -12084 // [-48, -52]

    System.out.println(Byte.toUnsignedInt((byte) -48));
    System.out.println(Byte.toUnsignedInt((byte) -52));

    final var buff = ByteBuffer.allocate(2).putShort((short) -12084);

    System.out.println(Arrays.toString(buff.array()));

  }
}
