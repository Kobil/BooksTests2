import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: ko6a
 * Date: 04.05.13
 * Time: 8:03
 * Email: ko6a93@bk.ru
 */
public class ExampleTest {
    public Scanner sc;
    public PrintWriter pr;
    public Random rand;

    public int MAX_LEN_BAG;
    public int n, b;
    public int[] a;
    public int[] cmb;
    public Bags[] allCombinations;

    int[] bag1;
    int[] bag2;
    int[] nullComb;
    int lenNullComb;
    int lenComb;

    public class Bags{
        public int[] comb;
        public int point = 1;
        public Boolean[] used;
    }

    public static void main(String[] argv) throws FileNotFoundException {
        ExampleTest test = new ExampleTest();
        test.run();
    }

    public void run() throws FileNotFoundException {
        sc = new Scanner(new File("input.txt"));
        pr = new PrintWriter(new File("output.txt"));
        input();
        getCombinations();
        solve();
        pr.close();
    }

    public void input() throws FileNotFoundException {
        n = sc.nextInt();
        a = new int[n];
        for(int i = 0; i < n; i++){
            a[i] = sc.nextInt();
        }
        b = sc.nextInt();

        MAX_LEN_BAG = (1 << n) - 1;
        allCombinations = new Bags[MAX_LEN_BAG];
        for(int i = 0; i < MAX_LEN_BAG; i++){
            allCombinations[i] = new Bags();
        }
    }

    public void output(int[] arr, int len){
        for(int i = 0; i < len; i++)
            pr.print(arr[i] + (arr[i] == 0 ? 1 : 0) + " ");

        pr.println();
    }

    public void getCombinations(){
        int i;
        int[] comb = new int[n];
        int x, y, combLen, point;
        for(i = 1; i <= MAX_LEN_BAG; i++){
            y = n - 1;
            combLen = 0;
            point = 1;
            for(x = i; x > 0; x >>= 1, y--){
                if (x % 2 == 1){
                    comb[combLen++] = y;
                    point *= a[y];
                    if (point < 0){
                        point = Integer.MAX_VALUE;
                        break;
                    }
                }
            }
            allCombinations[i - 1].comb = Arrays.copyOf(comb, combLen);
            allCombinations[i - 1].point = point;
        }
        Arrays.sort(allCombinations, new Comparator<Bags>() {
            @Override
            public int compare(Bags o1, Bags o2) {
                return o1.point - o2.point;
            }
        });
        for(i = 0; i < MAX_LEN_BAG; i++){
            if (allCombinations[i].point > b) break;
            allCombinations[i].used = new Boolean[allCombinations[i].point];
            Arrays.fill(allCombinations[i].used, false);
        }
        MAX_LEN_BAG = i;
    }

    //Получение перестановки через его порядковый номер и сохранение в cmb;
    public void getBagByNumber(int currentComb, int number){
        int point = allCombinations[currentComb].point;
        int lenComb = allCombinations[currentComb].comb.length;
        number++;
        for(int i = 0; i < lenComb; i++){
            point /= a[allCombinations[currentComb].comb[i]];
            cmb[i] = number / point + (number % point == 0 ? 0 : 1);
            number -= (cmb[i] - 1) * point;
        }
    }

    //Получение порядкового номера перестановки;
    public int getNumberOfBag(int currentComb, int[] bag){
        int number = 0;
        int point = allCombinations[currentComb].point;
        int combLen = allCombinations[currentComb].comb.length;
        for(int i = 0; i < combLen; i++){
            point /= a[allCombinations[currentComb].comb[i]];
            if (bag[allCombinations[currentComb].comb[i]] == 0) return -1;
            number += (bag[allCombinations[currentComb].comb[i]] - 1) * point;
        }
        return number;
    }

    public void inc(){
        bag2[nullComb[lenNullComb - 1]]++;
        for(int j = lenNullComb - 1; j > 0; j--){
            if(bag2[nullComb[j]] > a[nullComb[j]]) {
                bag2[nullComb[j]] = 1;
                bag2[nullComb[j - 1]]++;
            }
            else
                return;
        }
    }

    public void solve(){
        bag1 = new int[n];
        bag2 = new int[n];
        nullComb = new int[n];
        cmb = new int[n];
        int number, kk, maxNumber;
        int numbers1Len = 0;
        int numbers2Len = 0;
        int currentComb = 0;
        int[] numbers1 = new int[MAX_LEN_BAG];
        int[] numbers2 = new int[MAX_LEN_BAG];

        lenNullComb = 0;
        lenComb = 0;

        for(int i = 0; i < b; i++){
            int j = -1;
            while(currentComb < MAX_LEN_BAG){
                for(j = 0; j < allCombinations[currentComb].point; j++){
                    if(!allCombinations[currentComb].used[j]) break;
                }
                if(j == allCombinations[currentComb].point) currentComb++;
                else
                    break;
            }
            if (currentComb == MAX_LEN_BAG) break;

            getBagByNumber(currentComb, j);
            allCombinations[currentComb].used[j++] = true;

            Arrays.fill(bag1, 0);
            lenComb = allCombinations[currentComb].comb.length;
            for(int k = 0; k < lenComb; k++){
                bag1[allCombinations[currentComb].comb[k]] = cmb[k];
            }

            for(int k = currentComb + 1; k < MAX_LEN_BAG; k++){
                number = getNumberOfBag(k, bag1);
                if(number >= 0) {
                    allCombinations[k].used[number] = true;
                }
                else{
                    bag2 = Arrays.copyOf(bag1, n);
                    lenComb = allCombinations[k].comb.length;
                    lenNullComb = 0;

                    for(kk = 0; kk < lenComb; kk++){
                        if(bag1[allCombinations[k].comb[kk]] == 0){
                            nullComb[lenNullComb] = allCombinations[k].comb[kk];
                            lenNullComb++;
                            bag2[allCombinations[k].comb[kk]] = 1;
                        }
                    }

                    maxNumber = -1;
                    int point = -1;
                    numbers1Len = 0;
                    int num;
                    for(; bag2[nullComb[0]] <= a[nullComb[0]]; ){
                        kk = getNumberOfBag(k, bag2);
                        if(allCombinations[k].used[kk] == false){
                            if(maxNumber == -1){
                                maxNumber = kk;
                                numbers2Len = 0;
                                for(int nm = k + 1; nm < MAX_LEN_BAG; nm++){
                                    num = getNumberOfBag(nm, bag2);
                                    if (num >= 0){
                                        numbers1[numbers1Len++] = nm;
                                        if(allCombinations[nm].point != point){
                                            point = allCombinations[nm].point;
                                            numbers2[numbers2Len++] = 0;
                                        }
                                        if(allCombinations[nm].used[num] == false){
                                            numbers2[numbers2Len - 1]++;
                                        }
                                    }
                                }
                                continue;
                            }


                            point = -1;
                            int maxNum = 0;
                            numbers2Len = 0;

                            for(int nm = 0; nm < numbers1Len; nm++){
                                num = getNumberOfBag(numbers1[nm], bag2);
                                if(maxNumber == kk){
                                    if(allCombinations[numbers1[nm]].point != point){
                                        point = allCombinations[numbers1[nm]].point;
                                        numbers2[numbers2Len++] = 0;
                                    }
                                    if(allCombinations[numbers1[nm]].used[num] == false){
                                        numbers2[numbers2Len - 1]++;
                                    }
                                }
                                else{
                                    if(allCombinations[numbers1[nm]].point != point){
                                        if (numbers2Len > 0 && maxNum < numbers2[numbers2Len - 1]){
                                            break;
                                        }
                                        point = allCombinations[numbers1[nm]].point;
                                        numbers2Len++;
                                        maxNum = 0;
                                    }
                                    if(allCombinations[numbers1[nm]].used[num] == false){
                                        maxNum++;
                                        if(maxNum > numbers2[numbers2Len - 1]){
                                            numbers2[numbers2Len - 1] = maxNum;
                                            maxNumber = kk;
                                        }
                                    }
                                }
                            }
                        }
                        inc();
                    }
                    if(maxNumber == -1) continue;
                    getBagByNumber(k, maxNumber);
                    allCombinations[k].used[maxNumber] = true;
                    for(kk = 0; kk < allCombinations[k].comb.length; kk++){
                        bag1[allCombinations[k].comb[kk]] = cmb[kk];
                    }
                }
            }
            output(bag1, n);
        }
    }
}
