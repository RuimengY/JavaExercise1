import java.util.Random;
import java.util.Scanner;

public class MineSweeper {
    private int rows;
    private int cols;
    private int mine;
    private int mark;
    //board是一个点周围有多少地雷，如果该点是雷则为-1
    private int[][] board;
    //mines是地雷的图，有雷的地方是1，无雷的地方是0
    private int[][] mines;
    //show是玩游戏的人看到的有*的界面
    private int[][] show;
    private int[][] right;

    public MineSweeper() {
        //初始化长宽
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入地雷图的长宽以及地雷的个数，中间用空格间隔");
        while (true) {
            try {
                String s = sc.nextLine();
                String[] s1 = s.split(" ");
                if (s1.length != 3) {
                    throw new IllegalArgumentException("输入格式错误，请输入两个数，用空格分隔！");
                }
                rows = Integer.parseInt(s1[0]);
                cols = Integer.parseInt(s1[1]);
                mine = Integer.parseInt(s1[2]);
                if (rows < 0 || cols < 0) {
                    throw new ArrayIndexOutOfBoundsException("索引越界，请重新输入：");
                } else if (rows > 200 || cols > 200 || mine > rows * cols)
                    System.out.println("输入错误，请重新输入：");
                else {
                    board = new int[rows][cols];
                    mines = new int[rows][cols];
                    show = new int[rows][cols];
                    right = new int[rows][cols];
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入数字：");
            } catch (NegativeArraySizeException e) {
                System.out.println("请输入正数：");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //由输入的行列确定三张地图的坐标
    public void initialize() {
        //lab4时发现要初始化需要先让格点数均为0
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mines[i][j] = 0;
            }
        }
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
        //right为lab4新增地图，表示右键确定的点，初始化全为0，右键一下表示加1，如果偶数操作不管

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                show[i][j] = 1;
                right[i][j] = 0;
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

    //lab4重构，将正常状态下的页面展示display函数处理，跟PlayFinal相对应
    public void displayNormal() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                //输出+表示右键标记
                if (right[i][j] % 2 == 1)
                    System.out.print("+ ");
                else if (show[i][j] == 1)
                    System.out.print("# ");
                else if (show[i][j] == 0)
                    System.out.print(board[i][j] + " ");
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

        Scanner sc = new Scanner(System.in);
        int lr;
        for (int i = 0; ; i++) {
            if (FinishAll()) {
                System.out.println("恭喜完成扫雷!");
                break;
            }

            while (true) {
                try {
                    System.out.println("请输入选项，1表示左键，2表示右键：");
                    //每次循环都要重新输入是左键还是右键
                    //每次得到一个1或者2
                    while (true) {
                        //模拟左右键，标记雷的位置
                        //规定，左键为1，右键为2
                        lr = Integer.parseInt(sc.nextLine());
                        if (lr != 1 && lr != 2) System.out.println("输入有误，请重新输入:");
                        else break;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("输入错误，请重新输入");
                }
            }
            //进行的循环的次数是踩到雷或者全部扫完
            int x, y;
            //循环后得到一个x和一个y
            while (true) {
                try {
                    System.out.println("请输入x和y坐标，中间用空格分开");
                    String temp = sc.nextLine();
                    //x和y表示输入的位置
                    String[] number = temp.split(" ");
                    if (number.length != 2) {
                        throw new IllegalArgumentException("输入格式错误，请输入两个数，用空格分隔！");
                    }
                    x = Integer.parseInt(number[0]);
                    y = Integer.parseInt(number[1]);
                    //排除错误输入的情况
                    if (x < 0 || x >= rows || y < 0 || y >= cols) {
                        System.out.println("输入错误，请输入有效的行列：");
                    } else break;
                } catch (NumberFormatException e) {
                    System.out.println("输入格式错误，请输入整数！");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            //进行判断，是左键还是右键
            //左键和右键分别只做一次处理
            if (lr == 1) {
                //排除上来就是零的情况
                if (i == 0) {
                    while (board[x][y] == -1) {
                        //有可能的错误在于，没有清空一开始有数字的格点
                        initialize();

                    }
                    PlayOneTime(x, y, 1);
                } else {
                    PlayOneTime(x, y, 1);
                    //踩到雷，循环结束
                    if (board[x][y] == -1) break;
                }
            } else {
                RightOperation(x, y);
            }
        }
    }

    //FinishAll判断是否完全扫雷完毕
    //以点开的格子数来判断
    public boolean FinishAll() {
        int count = 0;
        //判断翻了的数量是否达到总数-地雷数
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (show[i][j] == 0)
                    count++;
            }
        }
        if (count == rows * cols - mine) return true;
        return false;
    }

    //lab4新增，右键的操作
    //新建了right的二维数组，用来表示右键的标志(初始全为0，如果为偶数不管，奇数进行标记)
    //新建了已经标记的地雷数
    public void RightOperation(int x, int y) {
        right[x][y]++;
        mark = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (right[i][j] % 2 == 1) {
                    mark++;
                }
            }
        }
        PlayOneTime(x, y, 2);
    }

    //lab3中打印showMap,以及点开的格子是数字/雷，并输出相应的话
    //show，1表示没点开，0表示点开了
    //right，初始为0，每改一次加一，如果是偶数就不变，奇数变
    //lab4新增参数，1表示左键，2表示右键
    public void PlayOneTime(int x, int y, int way) {
        System.out.println("已经标记的地雷数:" + mark);
        System.out.println("总地雷数" + mine);
        if (way == 2) displayNormal();
        else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (i == x && j == y) {
                        judge(x, y);
                    }
                }
            }
        }
    }


    //lab3中判断输入的x和y会发生什么，如果是雷就结束输出，如果不是雷正常
    public void judge(int x, int y) {
        if (board[x][y] == -1) {
            //失败
            displayFinal();
            System.out.println("踩中地雷，游戏结束");
        } else {
            //将不是雷的周围扫掉
            DeleteZero(x, y);
            displayNormal();
        }
    }

    //一次性将周围所有的不是地雷的点扫掉
    public void DeleteZero(int x, int y) {
        if (show[x][y] == 0) return;
        show[x][y] = 0;
        //判断周围的点是否要展示
        if (theNumberOfNeighbors(x, y) == 0) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i >= 0 && j >= 0 && i < rows && j < cols) {
                        DeleteZero(i, j);
                    }
                }
            }
        }
    }


}





