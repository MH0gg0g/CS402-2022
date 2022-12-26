package hill;

import java.math.BigInteger;

/**
 *
 * @author Mohammed
 */
public class hill {
    
    static final int N = 3;
    static String plain = "PAYMOREMONEY";
    static String cipher = "RRLMWBKASPDH";
    static String keyString = "rrfvsvcct";
    static int[][] keyMatrix = new int[N][N];
//    static int[][] keyMatrix = {{17, 17, 5}, {21, 18, 21}, {2, 2, 19}};
    static double[][] inverseMatrix = new double[N][N];
    
    public static void matrixNums(String key) {
        int z = 0;
        int mtxLength = keyMatrix.length;
        key = key.toLowerCase();
        
        for (int i = 0; i < mtxLength; i++)
            for (int j = 0; j < mtxLength; j++)
                keyMatrix[i][j] = (key.charAt(z++) - 'a');    
    }
    
    public static void getCofactor(int A[][], int temp[][], int p, int q, int n) {
        int i = 0, j = 0;

        for (int row = 0; row < n; row++)
        {
            for (int col = 0; col < n; col++)
            {

                if (row != p && col != q)
                {
                    temp[i][j++] = A[row][col];

                    // Row is filled, so increase row index and
                    // reset col index
                    if (j == n - 1)
                    {
                        j = 0;
                        i++;
                    }
                }
            }
        }
    }
 
    public static int determinant(int A[][], int n) {
        int D = 0; // Initialize result

        if (n == 1)
            return A[0][0];

        int [][]temp = new int[N][N]; // To store cofactors

        int sign = 1; // To store sign multiplier

        // Iterate for each element of first row
        for (int f = 0; f < n; f++) {
            // Getting Cofactor of A[0][f]
            getCofactor(A, temp, 0, f, n);
            D += sign * A[0][f] * determinant(temp, n - 1);

            // terms are to be added with alternate sign
            sign = -sign;
        }

        return D;
    }
 
// Function to get adjoint of A[N][N] in adj[N][N].
    public static void adjoint(int A[][],int [][]adj) { 
        if (N == 1) {
            adj[0][0] = 1;
            return;
        }

        // temp is used to store cofactors of A[][]
        int sign = 1;
        int [][]temp = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                // Get cofactor of A[i][j]
                getCofactor(A, temp, i, j, N);


                sign = ((i + j) % 2 == 0)? 1: -1;

                adj[j][i] = (sign)*(determinant(temp, N-1));
            }
        }
    }

    public static void matrixInverse(int A[][], double [][]inverse) {
        // Find determinant of A[][]
        int det = determinant(A, N);

        int [][]adj = new int[N][N];
        adjoint(A, adj);

        // Find Inverse using formula "inverse(A) = adj(A)/det(A)"
        int detInverse = new BigInteger("" + det).modInverse(new BigInteger("" + 26)).intValue();
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                inverse[i][j] = adj[i][j] * detInverse;
                inverse[i][j] += (int)(Math.ceil(inverse[i][j]/-26)*26);
                inverse[i][j] %= 26;
//                System.out.println((int)(Math.round(inverse[i][j]/26)*26));
            }
    }
    
    
    public static String encrypt(String plain, String key) {
        String cipherTxt = "";
        String plainTxt = plain;
        int [] plainvector = new int [3];
        int [] ciphervector = new int [3];
        int plainLength = plainTxt.length();
        plainTxt = plainTxt.toLowerCase();
        int k = 0;
        
        // padding
        
        while (plainLength % 3 != 0) {            
            plainTxt += "x";
            plainLength += 1;
        }
        
        // loop messages of length 3 
        while (k < plainLength) {
            // zeros ciphervector, intialize plainvector
            for (int i = 0; i < 3; i++) {
                plainvector[i] = plainTxt.charAt(k++) - 'a'; 
                ciphervector[i] = 0;
            }
             // matrix multiplication
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) 
                    ciphervector[i] += plainvector[j] * keyMatrix[j][i] ;
            }
            // calc mod, vector to string
            for (int i = 0; i < 3; i++) {
                ciphervector[i] %= 26;
                cipherTxt += (char)(ciphervector[i] + 'a');       
            }
        }
        // checks uppercase
        return (plain.toUpperCase().equals(plain)) ? cipherTxt.toUpperCase() : cipherTxt;
    }

    public static String decrypt(String cipher) {
        String plainTxt = "";
        String cipherTxt = cipher;
        int [] plainvector = new int [3];
        int [] ciphervector = new int [3];
        int cipherLength = cipherTxt.length();
        cipherTxt = cipherTxt.toLowerCase();
        int k = 0;
        
        // loop messages of length 3 
        while (k < cipherLength) {
            // zeros ciphervector, intialize plainvector
            for (int i = 0; i < 3; i++) {
                ciphervector[i] = cipherTxt.charAt(k++) - 'a'; 
                plainvector[i] = 0;
            }
             // matrix multiplication
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    plainvector[i] += ciphervector[j] * inverseMatrix[j][i] ;
                }
            }
            // calc mod, vector to string
            for (int i = 0; i < 3; i++) {
                plainvector[i] %= 26;
                plainTxt += (char)(plainvector[i] + 'a');       
            }
        }
        // checks uppercase
        return (cipher.toUpperCase().equals(cipher)) ? plainTxt.toUpperCase() : plainTxt;
    }

    public static String attack(String cipher) {
        String plainTxt = "";
        String cipherTxt = cipher;
        int [] plainvector = new int [3];
        int [] ciphervector = new int [3];
        int cipherLength = cipherTxt.length();
        cipherTxt = cipherTxt.toLowerCase();
        int k = 0;
        
        // loop messages of length 3 
        while (k < cipherLength) {
            // zeros ciphervector, intialize plainvector
            for (int i = 0; i < 3; i++) {
                ciphervector[i] = cipherTxt.charAt(k++) - 'a'; 
                plainvector[i] = 0;
            }
             // matrix multiplication
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    plainvector[i] += ciphervector[j] * inverseMatrix[j][i] ;
                }
            }
            // calc mod, vector to string
            for (int i = 0; i < 3; i++) {
                plainvector[i] %= 26;
                plainTxt += (char)(plainvector[i] + 'a');       
            }
        }
        // checks uppercase
        return (cipher.toUpperCase().equals(cipher)) ? plainTxt.toUpperCase() : plainTxt;
    }
    
    
    public static void main(String[] args) {
        // key from string to matrix
        matrixNums(keyString);
        
        // calculate matrix inverse 
        matrixInverse(keyMatrix, inverseMatrix);

        // encryption example 
//        System.out.println(encrypt(plain, keyString));
        
        // decryption example
//        System.out.println(decrypt(cipher));

        
        // encrypting then decrypting same text at the same time
        System.out.println(decrypt(encrypt(plain, keyString)));
        

        // displaying inverse matrix
        System.out.println("\n\nINVERSE MATRIX");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(inverseMatrix[i][j]+ " ");
            }
            System.out.println();
        }
    }
    
}
