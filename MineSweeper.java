import java.util.Random;
import java.util.Scanner;

public class MineSweeper {
    private int rows;
    private int cols;
    private int mine;
    //board是一个点周围有多少地雷，如果该点是雷则为-1
    private int[][] board;
    //mines是地雷的图，有雷的地方是1，无雷的地方是0
    private int[][] mines;
    //show是玩游戏的人看到的有*的界面
    private int[][] show;


    public MineSweeper() {
        //初始化长宽
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        String[] s1 = s.split(" ");
        rows = Integer.parseInt(s1[0]);
        cols = Integer.parseInt(s1[1]);
        mine = Integer.parseInt(s1[2]);
        board = new int[rows][cols];
        mines = new int[rows][cols];
        show = new int[rows][cols];
    }


    //由输入的行列确定三张地图的坐标
    public void initialize() {
        //mines是地雷的图，显示有雷的位置是1，其余没有雷的位置是0
        Random r = new Random();
        int number = 0;
        while (number < mine) {
            int rx = r.nextInt(rows);
            int ry = r.nextInt(cols);
            if (mines[rx][ry] != 1) {
                mines[rx][ry] = 1;
                number++;
            }
        }

        //show地图里面的数字全为1（打印的时候为#，输入命令的时候该格点位置处的数字(ShowMap)被打印出来
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                show[i][j] = 1;
            }
        }

        //lab2中要求统计一个格点周围有多少地雷,如果是地雷则为-1
        //board地图
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //表示地雷的map中该点是地雷的点
                if (mines[i][j] == 1)
                    board[i][j] = -1;
                    //表示格点周围展示有多少地雷
                else board[i][j] = theNumberOfNeighbors(i, j);
            }
        }
    }

    //lab2中统计一个格点周围的地雷数量
    public int theNumberOfNeighbors(int x, int y) {
        int num = 0;
        if (x > 0 && mines[x - 1][y] == 1) num++;
        if (x < rows - 1 && mines[x + 1][y] == 1) num++;
        if (y > 0 && mines[x][y - 1] == 1) num++;
        if (y < cols - 1 && mines[x][y + 1] == 1) num++;
        if (x > 0 && y > 0 && mines[x - 1][y - 1] == 1) num++;
        if (x > 0 && y < cols - 1 && mines[x - 1][y + 1] == 1) num++;
        if (x < rows - 1 && y > 0 && mines[x + 1][y - 1] == 1) num++;
        if (x < rows - 1 && y < cols - 1 && mines[x + 1][y + 1] == 1) num++;
        return num;
    }

    //lab2中实现打印一个地雷以及周围地雷数量
    //这是游戏输了之后的界面(展示全部界面)
    public void displayFinal() {
        //System.out.println("欢迎来到杨蕊萌制作的扫雷游戏，打印的地雷图如下：");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == -1) System.out.print("*" + " ");
                else System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    //展示给用户的页面
    public void Play() {
        initialize();
        //一开始全是#的界面
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print("# ");
            }
            System.out.println();
        }

        //lab3中设置循环，直到剩余0个没点的位置
        // 要进行循环的次数
        int num = rows * cols - mine;
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < num; i++) {
            int x, y;
            while (true) {
                //temp用来暂时表示单击左键的命令
                String temp = sc.nextLine();
                //x和y表示输入的位置
                x = temp.charAt(0) - '0';
                y = temp.charAt(2) - '0';
                //排除错误输入的情况
                if (x < 0 || x >= rows || y < 0 || y >= cols) {
                    System.out.println("Invalid input. Please enter valid row and column.");
                } else break;
            }
            //排除第一次就是雷的情况
            if (i == 0) {
                while (board[x][y] == -1) {
                    System.out.println("开始初始化");
                    initialize();
                    System.out.println("进行了一次初始化");
                }
                System.out.println("开始游戏");
                PlayOneTime(x, y);
            } else PlayOneTime(x, y);
        }
        System.out.println("恭喜你完成扫雷");

    }

    //lab3中打印showMap,以及点开的格子是数字/雷，并输出相应的话
    //show，1表示没点开，0表示点开了
    public void PlayOneTime(int x, int y) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == x && j == y) {
                    judge(x, y);
                }
                if (show[i][j] == 1)
                    System.out.print("# ");
                else if (show[i][j] == 0)
                    System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }

    }

    //lab3中判断输入的x和y会发生什么，如果是雷就结束输出，如果不是雷正常
    public void judge(int x, int y) {
        if (board[x][y] == -1) {
            //失败
            displayFinal();
            System.out.println("踩中地雷，游戏结束");
            System.exit(1);
        } else
            //将不是雷的周围扫掉
            DeleteZero(x, y);

    }

    //一次性将周围所有的不是地雷的点扫掉
    public void DeleteZero(int x, int y) {
        if (show[x][y] == 0) return;
        show[x][y] = 0;
        //判断周围的点是否要展示
        if (theNumberOfNeighbors(x, y) == 0) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i >= 0 && j >= 0 && i < rows && j < cols ) {
                        DeleteZero(i, j);
                    }
                }
            }
        }
    }
}





