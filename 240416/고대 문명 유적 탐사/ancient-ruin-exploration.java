import java.util.*;
import java.io.*;

/* 유적지 5x5 유물조각 1~7 */
/* 3x3 격자 선택 */
public class Main {

    static int[] xDiff = {-1, 0, 0, 1};
    static int[] yDiff = {0, -1, 1, 0};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        int K = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        int[][] map = new int[5][5];

        for(int i=0;i<5;i++) {
            st = new StringTokenizer(br.readLine(), " ");
            for(int j=0;j<5;j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine(), " ");
        int[] numList = new int[M];
        int cur = 0;
        for(int i=0;i<M;i++) {
            numList[i] = Integer.parseInt(st.nextToken());
        }

        int answer = 0;
        for(int turn = 0;turn<K;turn++) {
            //#1. 탐사 진행
            int[][] nextMap = null;
            int maxPoint = 0;
            for(int k=0;k<3;k++) {
                for(int j=0;j<3;j++) {
                    for(int i=0;i<3;i++) {
                        int[][] tmp = turn(map, i, j, k);
                        int point = findFirst(tmp);
                        if(maxPoint < point) {
                            maxPoint = point;
                            nextMap = tmp;
                        }
                    }
                }
            }
            if(maxPoint == 0) {
                return;
            }
            map = nextMap;
            //answer += maxPoint;

            //#2. 유물 획득
            int[] result = run(map, numList, cur);
            answer += result[0];
            cur = result[1];
            
            System.out.print(result[0]+ " ");
        }

        System.out.println();
    }

    static int[][] turn(int[][] map, int startY, int startX, int direction) {
        int[][] newMap = copyOf(map);

        //90도
        if(direction == 0) {
            for(int i=startY;i<startY+3;i++) {
                for(int j=startX;j<startX+3;j++) {
                    newMap[i][j] = map[4 - j][i];
                }
            }
        }
        //180도
        else if(direction == 1) {
            for(int i=0;i<3;i++) {
                for(int j=0;j<3;j++) {
                    newMap[startY + i][startX + j] = map[startY + 2 - i][startX + 2 - j];
                }
            }
        }
        //270도
        else {
            for(int i=startY;i<startY+3;i++) {
                for(int j=startX;j<startX+3;j++) {
                    newMap[i][j] = map[j][4 - i];
                }
            }
        }

        return newMap;
    }

    static int[][] copyOf(int[][] map) {
        int[][] newMap = new int[5][5];
        for(int i=0;i<5;i++)
            newMap[i] = Arrays.copyOf(map[i], 5);
        return newMap;
    }

    //찾은 점수를 반환
    static int findFirst(int[][] map) {
        int visited = 0;
        int sum = 0;
        for(int i=0;i<5;i++) {
            for(int j=0;j<5;j++) {
                int bit = (1 << (i * 5 + j));
                if((visited & bit) == bit)
                    continue;
                
                visited |= bit;
                int target = map[i][j];
                int cnt = 0;
                Queue<int[]> queue = new ArrayDeque<>();
                queue.add(new int[]{i, j});
                while(!queue.isEmpty()) {
                    int[] p = queue.poll();
                    cnt++;
                    for(int k=0;k<4;k++) {
                        int y = p[0] + yDiff[k];
                        int x = p[1] + xDiff[k];
                        int nextBit = (1 << (y * 5 + x));
                        if(isBoundary(y, x) || (visited & nextBit) == nextBit || map[y][x] != target)
                            continue;
                        visited |= nextBit;
                        queue.add(new int[]{y, x});
                    }
                }

                if(cnt >= 3) {
                    sum+=cnt;
                }
            }
        }

        return sum;
    }

    static int[] run(int[][] map, int[] fillMap, int cur) {
        int sum = 0;
        int prevSum = -1;
        while(sum > prevSum ) {
            prevSum = sum;
            int visited = 0;
            LinkedList<int[]> fillList = new LinkedList<>();
            for(int i=4;i>=0;i--) {
                for(int j=0;j<5;j++) {
                    int bit = (1 << (i * 5 + j));
                    if((visited & bit) == bit)
                        continue;
                    
                    visited |= bit;
                    int target = map[i][j];
                    int cnt = 0;
                    Queue<int[]> queue = new ArrayDeque<>();
                    queue.add(new int[]{i, j});
                    LinkedList<int[]> fillList2 = new LinkedList<>();
                    while(!queue.isEmpty()) {
                        int[] p = queue.poll();
                        cnt++;
                        
                        fillList2.add(new int[]{p[0], p[1]});
                        for(int k=0;k<4;k++) {
                            int y = p[0] + yDiff[k];
                            int x = p[1] + xDiff[k];
                            int nextBit = (1 << (y * 5 + x));
                            if(isBoundary(y, x) || (visited & nextBit) == nextBit || map[y][x] != target)
                                continue;
                            visited |= nextBit;
                            queue.add(new int[]{y, x});
                        }
                    }

                    if(cnt >= 3) {
                        sum+=cnt;
                        fillList2.sort((o1, o2) -> {
                            if(o1[1] == o2[1]) {
                                return o2[0] - o1[0];
                            }
                            return o1[1] - o2[1];
                        });
                        fillList.addAll(fillList2);
                    }
                }
            }

            //System.out.println("fillList");
            for(int[] next : fillList) {
                map[next[0]][next[1]] = fillMap[cur++];
                //System.out.println(Arrays.toString(next) + " " + map[next[0]][next[1]]);
            }
        }

        return new int[]{sum, cur};
    }

    static boolean isBoundary(int y, int x) {
        return y < 0 || y >= 5 || x < 0 || x >= 5;
    }
}