import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        //lab4新增功能，在游戏玩完之后可以选择再来一局或者退出,实际为最后的方法
        System.out.println("欢迎来到杨蕊萌的扫雷游戏");
        int choice = 1;
        while (choice == 1) {
            MineSweeper ms = new MineSweeper();
            Scanner sc = new Scanner(System.in);
            ms.Play();
            System.out.println("请选择：1为重新来一局，0为游戏结束");
            choice = sc.nextInt();
        }
    }

}
