public class PogoretskiyJosephHillCipher {

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

  public static int[] encrypt(int plaintext[], int encryptionKey[][]) {

    // Pad plaintext with 'Z' if neccesary.
    int[] newPlaintext;
    if (plaintext.length % 2 == 1) {
      newPlaintext = new int[plaintext.length + 1];
      for (int i = 0; i < plaintext.length; i++) {
        newPlaintext[i] = plaintext[i];
      }
      newPlaintext[newPlaintext.length - 1] = 25;
    } else {
      newPlaintext = new int[plaintext.length];
      for (int i = 0; i < plaintext.length; i++) {
        newPlaintext[i] = plaintext[i];
      }
    }

    // Convert plaintext into an array of pairs.
    int[][] plainInPairs = new int[newPlaintext.length / 2][2];
    int row = 0;
    int col = 0;
    for (int i = 0; i < newPlaintext.length; i++) {
      plainInPairs[row][col] = newPlaintext[i];
      if (++col == 2) {
        col = 0;
        row += 1;
      }
    }

    // Multiply and mod26 encryption key to each plaintext pair.
    for (int i = 0; i < plainInPairs.length; i++) {
      int firstVal = plainInPairs[i][0];
      int secondVal = plainInPairs[i][1];

      plainInPairs[i][0] = mod26(encryptionKey[0][0] * firstVal + encryptionKey[0][1] * secondVal);
      plainInPairs[i][1] = mod26(encryptionKey[1][0] * firstVal + encryptionKey[1][1] * secondVal);
    }

    // Convert pairs back into an integer array.
    int count = 0;
    for (int i = 0; i < plainInPairs.length; i++) {
      for (int j = 0; j < plainInPairs[i].length; j++) {
        newPlaintext[count] = plainInPairs[i][j];
        count++;
      }
    }

    return newPlaintext;
  }

  public static int[] decrypt(int ciphertext[], int decryptionKey[][]) {
    // Convert plaintext into an array of pairs.
    int[][] plainInPairs = new int[ciphertext.length / 2][2];
    int row = 0;
    int col = 0;
    for (int i = 0; i < ciphertext.length; i++) {
      plainInPairs[row][col] = ciphertext[i];
      if (++col == 2) {
        col = 0;
        row += 1;
      }
    }

    // Multiply and mod26 encryption key to each plaintext pair.
    for (int i = 0; i < plainInPairs.length; i++) {
      int firstVal = plainInPairs[i][0];
      int secondVal = plainInPairs[i][1];

      plainInPairs[i][0] = mod26(decryptionKey[0][0] * firstVal + decryptionKey[0][1] * secondVal);
      plainInPairs[i][1] = mod26(decryptionKey[1][0] * firstVal + decryptionKey[1][1] * secondVal);
    }

    // Convert pairs back into an integer array.
    int count = 0;
    for (int i = 0; i < plainInPairs.length; i++) {
      for (int j = 0; j < plainInPairs[i].length; j++) {
        ciphertext[count] = plainInPairs[i][j];
        count++;
      }
    }

    return ciphertext;
  }

  public static void main(String args[]) {

    // Create an English alphabet (uppercase) mapped to indicies 0-25.
    char[] alphabet = new char[26];
    for (int i = 0; i < 26; i++) {
      alphabet[i] = (char) ('A' + i);
    }

    // Setup for testing.
    int[][] encryptionKey = { { 16, 7 }, { 9, 14 } };
    String plaintext = "JMUISCOOL";
    String ciphertext = "MQGVGQSMJI";

    // Convert plaintext String into an array of integers.
    int[] plaintextInt = new int[plaintext.length()];
    for (int i = 0; i < plaintext.length(); i++) {
      for (int j = 0; j < alphabet.length; j++) {
        if (alphabet[j] == plaintext.charAt(i)) {
          plaintextInt[i] = j;
          break;
        }
      }
    }

    // Convert ciphertext String into an array of integers.
    int[] ciphertextInt = new int[ciphertext.length()];
    for (int i = 0; i < ciphertext.length(); i++) {
      for (int j = 0; j < alphabet.length; j++) {
        if (alphabet[j] == ciphertext.charAt(i)) {
          ciphertextInt[i] = j;
          break;
        }
      }
    }

    /* Test finding the decryption key */
    System.out.println("(1) Finding the decryption key:\n");
    int[][] decryptionKey = findDecryptionKey(encryptionKey);

    // Print encryption key
    System.out.println("      Encryption Key:   ");
    for (int i = 0; i < encryptionKey.length; i++) {
      System.out.print("                          ");
      for (int j = 0; j < encryptionKey[i].length; j++) {
        System.out.print(encryptionKey[i][j] + "  ");
      }
      System.out.println("");
    }

    // Print decryption key
    System.out.println("\n      Decryption Key:   ");
    for (int i = 0; i < decryptionKey.length; i++) {
      System.out.print("                          ");
      for (int j = 0; j < decryptionKey[i].length; j++) {
        System.out.print(decryptionKey[i][j] + "  ");
      }
      System.out.println();
    }

    /* Test encryption */
    System.out.println("\n(2) Encrypting the following message: " + plaintext + "\n");
    int[] cipheredText = encrypt(plaintextInt, encryptionKey);
    String cipherString = "";
    for (int i = 0; i < cipheredText.length; i++) {
      cipherString += (alphabet[cipheredText[i]]);
    }
    System.out.println("      Ciphertext:         " + cipherString + "\n");

    /* Test decryption */
    System.out.println("(3) Decrypting the following message: " + ciphertext + "\n");
    int[] decrypted = decrypt(ciphertextInt, decryptionKey);
    String decryptedString = "";
    for (int i = 0; i < decrypted.length; i++) {
      decryptedString += (alphabet[decrypted[i]]);
    }
    System.out.println("      Decrypted:          " + decryptedString + "\n");
  }

}