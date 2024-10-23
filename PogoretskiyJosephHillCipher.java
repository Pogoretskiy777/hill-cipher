public class PogoretskiyJosephHillCipher {

  char[] alphabet = new char[26];

  public static int xgcd(int inE, int inZ) {
    int t1 = 0;
    int t2 = 1;
    int inZcopy = inZ; // Copy to add to negative answer
    int quotient;
    int temp; // Used for swapping values

    // Extended Euclid's algorithm that's iterative
    while (inZ != 0) {
      quotient = inE / inZ; // Divide
      temp = inZ;
      inZ = inE % inZ; // Get remainder
      inE = temp;

      temp = t1;
      t1 = t2 - (quotient * t1);
      t2 = temp;
    }

    // If the multiplicative inverse does not exist
    if (inE != 1 || t2 == 1) {
      return -1;
    }

    // If answer is negative, add inZ
    if (t2 < 0) {
      t2 += inZcopy;
    }

    return t2;
  }

  public static int mod26(int number) {
    int result = number % 26;
    if (result < 0) {
      result += 26;
    }
    return result;
  }

  public static int[][] findDecryptionKey(int encryptionKey[][]) {
    int a = encryptionKey[0][0];
    int b = encryptionKey[0][1];
    int c = encryptionKey[1][0];
    int d = encryptionKey[1][1];

    int determinant = (a * d) - (b * c);

    // Check if multiplicative inverse exists
    if (determinant == 0) {
      return null;
    }

    determinant = xgcd(determinant, 26);

    int[][] decryptionKey = new int[2][2];

    decryptionKey[0][0] = (mod26(d * determinant));
    decryptionKey[0][1] = (mod26(-b * determinant));
    decryptionKey[1][0] = (mod26(-c * determinant));
    decryptionKey[1][1] = (mod26(a * determinant));

    return decryptionKey;
  }

  public int[] encrypt(int plaintext[], int encryptionKey[][]) {
    throw new IllegalStateException("Result not ready");
  }

  public int[] decrypt(int ciphertext[], int encryptionKey[][]) {
    throw new IllegalStateException("Result not ready");
  }

  public static void main(String args[]) {
    int[][] encryptionKey = { { 16, 7 }, { 9, 14 } };
    char[] plaintext = { 'J', 'M', 'U', 'I', 'S', 'C', 'O', 'O', 'L' };
    char[] ciphertext = { 'M', 'Q', 'G', 'V', 'G', 'Q', 'S', 'M', 'J', 'I' };

    int[][] result = findDecryptionKey(encryptionKey);

    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        System.out.print(result[i][j] + " ");
      }
      System.out.println();
    }
  }

}