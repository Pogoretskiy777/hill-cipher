import java.util.*;
import java.io.*;

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
    int[] copiedCiphertext = new int[ciphertext.length];
    System.arraycopy(ciphertext, 0, copiedCiphertext, 0, ciphertext.length);

    // Convert plaintext into an array of pairs.
    int[][] plainInPairs = new int[copiedCiphertext.length / 2][2];
    int row = 0;
    int col = 0;
    for (int i = 0; i < copiedCiphertext.length; i++) {
      plainInPairs[row][col] = copiedCiphertext[i];
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
        copiedCiphertext[count] = plainInPairs[i][j];
        count++;
      }
    }

    return copiedCiphertext;
  }

  public static void crackCipher(int[] ciphertext) {
    // Create an English alphabet (uppercase) mapped to indicies 0-25.
    char[] alphabet = new char[26];
    for (int i = 0; i < 26; i++) {
      alphabet[i] = (char) ('A' + i);
    }
    try {
      FileWriter fw = new FileWriter("cracked-results.txt", false);

      int count = 0;
      for (int i = 0; i < 26; i++) {
        for (int j = 0; j < 26; j++) {
          for (int k = 0; k < 26; k++) {
            for (int l = 0; l < 26; l++) {
              if ((i * l) - (j * k) == 0) {
                continue;
              }
              int[][] decryptionKey = { { i, j }, { k, l } };
              int[] result = decrypt(ciphertext, decryptionKey);
              String decryptedString = "";
              for (int m = 0; m < result.length; m++) {
                decryptedString += (alphabet[result[m]]);
              }
              for (int n = 0; n < decryptedString.length(); n++) {
                fw.write(decryptedString.charAt(n));
              }
              fw.write("\n");
              if (count == 60177) { // Line in .txt file where cleartext I found.
                System.out.println(
                    "(Bonus) Decrypt the following ciphertext: HDHNXZHFGWVKPOBOHFCBKYWGATXUNNOXJFUUXZLUDXLKECNOPINYJRAQYOXPVKTMTBUELRNTKOCBKYXANTHNKIHFOJ\n");
                System.out.println("      Decryption key: ");
                for (int o = 0; o < decryptionKey.length; o++) {
                  System.out.print("                          ");
                  for (int p = 0; p < decryptionKey[o].length; p++) {
                    System.out.print(decryptionKey[o][p] + " ");
                  }
                  System.out.println();
                }

                int[][] encryptionKey = findDecryptionKey(decryptionKey);

                // Print decryption key
                System.out.println("\n      Encryption Key:   ");
                for (int q = 0; q < encryptionKey.length; q++) {
                  System.out.print("                          ");
                  for (int r = 0; r < encryptionKey[q].length; r++) {
                    System.out.print(encryptionKey[q][r] + "  ");
                  }
                  System.out.println();
                }
                System.out.println("\nCleartext: " + decryptedString);
              }
              count++;
            }
          }
        }
      }
      fw.close();
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

  public static void searchAndSaveCommonWordLines(String inputFilePath, String outputFilePath, String searchTerm) {
    List<String> matchingLines = new ArrayList<>();

    // Search and save matching lines to an array
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(searchTerm)) {
          matchingLines.add(line);
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading the input file: " + e.getMessage());
      return;
    }

    // Write the matching lines to the output file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, true))) {
      for (String matchingLine : matchingLines) {
        writer.write(matchingLine);
        writer.newLine();
      }
    } catch (IOException e) {
      System.err.println("Error writing to the output file: " + e.getMessage());
    }

    System.out.println("Matching lines have been saved to: " + outputFilePath);
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

    /* Crack ciphertext */
    String cipherToCrack = "HDHNXZHFGWVKPOBOHFCBKYWGATXUNNOXJFUUXZLUDXLKECNOPINYJRAQYOXPVKTMTBUELRNTKOCBKYXANTHNKIHFOJ";

    // Convert ciphertext String into an array of integers.
    int[] cipherCrackInt = new int[cipherToCrack.length()];
    for (int i = 0; i < cipherToCrack.length(); i++) {
      for (int j = 0; j < alphabet.length; j++) {
        if (alphabet[j] == cipherToCrack.charAt(i)) {
          cipherCrackInt[i] = j;
          break;
        }
      }
    }
    crackCipher(cipherCrackInt);
    System.err.println();

    // // Read and uppercase common words
    // Set<String> commonWords = new HashSet<>();
    // try (BufferedReader commonWordsReader = new BufferedReader(new
    // FileReader("common-5.txt"))) {
    // String word;
    // while ((word = commonWordsReader.readLine()) != null) {
    // commonWords.add(word.toUpperCase().trim());
    // }
    // } catch (IOException e) {
    // System.err.println("Error reading the common words file: " + e.getMessage());
    // return;
    // }
    // String inputFilePath = "cracked-results.txt";
    // String outputFilePath = "filtered.txt";

    // for (String word : commonWords) {
    // searchAndSaveCommonWordLines(inputFilePath, outputFilePath, word);
    // }
  }

}