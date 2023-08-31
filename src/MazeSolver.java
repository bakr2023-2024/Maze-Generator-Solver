
import java.util.*;

public class MazeSolver {
	int[][] map;
	public static volatile boolean keepSolving = true;
	boolean found = false;
	Random rand = new Random(System.nanoTime());
	public static volatile int delay = 100;

	public interface Observer {
		public void updateMaze(int[][] arr);
	}

	ArrayList<Observer> observers = new ArrayList<Observer>();

	public void addObserver(Observer observer) {
		this.observers.add(observer);
	}

	public void removeObserver(Observer observer) {
		this.observers.remove(observer);
	}

	public void notifyObservers() {
		for (Observer observer : this.observers) {
			observer.updateMaze(this.map);
		}
	}

	class Vertex {
		int x;
		int y;
		Vertex prev;
		int head = 0;

		Vertex(int x, int y, Vertex prev) {
			this.x = x;
			this.y = y;
			this.prev = prev;
		}

		public String toString() {
			return x + "," + y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || this.getClass() != obj.getClass())
				return false;
			if (this == obj)
				return true;
			Vertex test = (Vertex) obj;
			return this.x == test.x && this.y == test.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.x, this.y);
		}
	}

	private boolean collide(int sx, int sy) {
		if ((sx >= 0 && sx < this.map.length) && (sy >= 0 && sy < this.map[0].length)
				&& (this.map[sx][sy] == -3 || this.map[sx][sy] == 0 || this.map[sx][sy] == -2))
			return false;
		return true;
	}

	private boolean wallcollide(int sx, int sy) {
		if ((sx >= 0 && sx < this.map.length) && (sy >= 0 && sy < this.map[0].length) && (this.map[sx][sy] != 1)
				&& (this.map[sx][sy] != -6))
			return false;
		return true;
	}

	private void constructPath(Vertex current) {
		if (current == null)
			return;
		constructPath(current.prev);
		if (this.map[current.x][current.y] != 1)
			this.map[current.x][current.y] = -5;
	}

	private void constructPath(AVertex current) {
		if (current == null)
			return;
		constructPath(current.parent);
		this.map[current.x][current.y] = -5;
	}

	private Queue<Vertex> queue = new LinkedList<Vertex>();
	private Vertex location = null;

	public void solveMaze(int[][] arr, int sx, int sy, int ex, int ey, String type) {
		this.map = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++) {
			this.map[i] = arr[i].clone();
		}
		Vertex curr = null;

		this.map[sx][sy] = -2;
		this.map[ex][ey] = -3;
		notifyObservers();
		if (type.equals("BFS")) {
			this.queue.offer(new Vertex(sx, sy, null));
			curr = BFS(this.queue.peek());
		} else if (type.equals("DFS")) {
			DFS(new Vertex(sx, sy, null));
			curr = this.location;
		} else if (type.equals("A*") || (type.equals("Best"))) {
			AVertex current = bestStar(new AVertex(sx, sy, null), new AVertex(ex, ey, null), type);
			constructPath(current);
			notifyObservers();
			return;
		} else if (type.equals("WallFollower")) {
			curr = wallFollower(new Vertex(sx, sy, null));
		} else if (type.equals("Pledge")) {
			curr = pledge(new Vertex(sx, sy, null));
		} else if (type.equals("Chain")) {
			curr = chain(new Vertex(sx, sy, null), new Vertex(ex, ey, null));
		} else if (type.equals("Trémaux")) {
			curr = tremaux(new Vertex(sx, sy, null));
		} else if (type.equals("Dead-end filling")) {
			curr = deadend(new Vertex(sx, sy, null));
		}
		constructPath(curr);
		notifyObservers();
	}

	private void delay() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private Vertex BFS(Vertex curr) {
		while (!this.queue.isEmpty() && keepSolving) {
			curr = this.queue.poll();
			if (this.map[curr.x][curr.y] == -3) {
				this.found = true;
				return curr;
			}
			this.map[curr.x][curr.y] = -1;
			if (!collide(curr.x + 1, curr.y)) {
				this.queue.add(new Vertex(curr.x + 1, curr.y, curr));
			}
			if (!collide(curr.x - 1, curr.y)) {
				this.queue.add(new Vertex(curr.x - 1, curr.y, curr));
			}
			if (!collide(curr.x, curr.y + 1)) {
				this.queue.add(new Vertex(curr.x, curr.y + 1, curr));
			}
			if (!collide(curr.x, curr.y - 1)) {
				this.queue.add(new Vertex(curr.x, curr.y - 1, curr));
			}
			notifyObservers();
			delay();
		}
		return null;
	}

	private void DFS(Vertex curr) {
		Vertex x;
		if (this.map[curr.x][curr.y] == -1 || this.found || !keepSolving)
			return;
		if (this.map[curr.x][curr.y] == -3) {
			this.location = curr;
			this.found = true;
			return;
		}
		this.map[curr.x][curr.y] = -1;
		notifyObservers();
		delay();
		if (!collide(curr.x + 1, curr.y)) {
			x = new Vertex(curr.x + 1, curr.y, curr);
			DFS(x);
		}
		if (!collide(curr.x - 1, curr.y)) {
			x = new Vertex(curr.x - 1, curr.y, curr);
			DFS(x);
		}
		if (!collide(curr.x, curr.y + 1)) {
			x = new Vertex(curr.x, curr.y + 1, curr);
			DFS(x);
		}
		if (!collide(curr.x, curr.y - 1)) {

			x = new Vertex(curr.x, curr.y - 1, curr);
			DFS(x);
		}
	}

	class AVertex {
		int gn;
		int hn;
		int fn;
		int x;
		int y;
		AVertex parent;

		AVertex(int x, int y, AVertex prev) {
			this.x = x;
			this.y = y;
			this.parent = prev;
			this.gn = (prev == null) ? 0 : prev.gn + 1;
		}

		public void updateStats(int x, int y) {
			this.hn = Math.abs(this.x - x) + Math.abs(this.y - y);
			this.fn = this.hn + this.gn;
		}

		public String toString() {
			return this.x + "," + this.y + "\ng(n): " + this.gn + "\nh(n): " + this.hn + "\nf(n): " + this.fn;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || this.getClass() != obj.getClass())
				return false;
			if (this == obj)
				return true;
			AVertex test = (AVertex) obj;
			return this.x == test.x && this.y == test.y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.x, this.y);
		}
	}

	HashSet<AVertex> visited = new HashSet<AVertex>();

	private AVertex bestStar(AVertex start, AVertex end, String type) {
		start.updateStats(end.x, end.y);
		PriorityQueue<AVertex> pq;
		if (type.equals("A*"))
			pq = new PriorityQueue<AVertex>(new Comparator<AVertex>() {
				@Override
				public int compare(AVertex v1, AVertex v2) {
					return v1.fn - v2.fn;
				}
			});
		else
			pq = new PriorityQueue<AVertex>(new Comparator<AVertex>() {
				@Override
				public int compare(AVertex v1, AVertex v2) {
					return v1.hn - v2.hn;
				}
			});
		pq.add(start);
		while (!pq.isEmpty()) {
			if (!keepSolving)
				return null;
			AVertex curr = pq.poll();
			if (this.visited.contains(curr))
				continue;
			this.map[curr.x][curr.y] = -1;
			this.visited.add(curr);
			if (curr.equals(end)) {
				this.found = true;
				return curr;
			}
			if (!collide(curr.x + 1, curr.y) && !this.visited.contains(new AVertex(curr.x + 1, curr.y, null))) {
				AVertex neighbor = new AVertex(curr.x + 1, curr.y, curr);
				neighbor.updateStats(end.x, end.y);
				pq.add(neighbor);
			}
			if (!collide(curr.x - 1, curr.y) && !this.visited.contains(new AVertex(curr.x - 1, curr.y, null))) {
				AVertex neighbor = new AVertex(curr.x - 1, curr.y, curr);
				neighbor.updateStats(end.x, end.y);
				pq.add(neighbor);
			}
			if (!collide(curr.x, curr.y + 1) && !this.visited.contains(new AVertex(curr.x, curr.y + 1, null))) {
				AVertex neighbor = new AVertex(curr.x, curr.y + 1, curr);
				neighbor.updateStats(end.x, end.y);
				pq.add(neighbor);
			}
			if (!collide(curr.x, curr.y - 1) && !this.visited.contains(new AVertex(curr.x, curr.y - 1, null))) {
				AVertex neighbor = new AVertex(curr.x, curr.y - 1, curr);
				neighbor.updateStats(end.x, end.y);
				pq.add(neighbor);
			}
			notifyObservers();
			delay();
		}
		return null;
	}

	private Vertex move(Vertex curr, int direction) {
		if (direction == 1) {
			if (curr.head == 0) {
				if (!wallcollide(curr.x, curr.y + 1)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x - 1, curr.y) && wallcollide(curr.x, curr.y + 1)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}

				Vertex k = new Vertex(curr.x - 1, curr.y, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 1) {
				if (!wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
				Vertex k = new Vertex(curr.x, curr.y + 1, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 2) {
				if (!wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x + 1, curr.y)
						&& wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x + 1, curr.y) && wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}

				Vertex k = new Vertex(curr.x + 1, curr.y, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 3) {
				if (!wallcollide(curr.x - 1, curr.y)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y - 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x - 1, curr.y) && wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}

				Vertex k = new Vertex(curr.x, curr.y - 1, curr);
				k.head = curr.head % 4;
				return k;
			} else {
				return curr;
			}
		} else {
			if (curr.head == 0) {
				if (!wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y - 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x, curr.y + 1)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x - 1, curr.y) && wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				Vertex k = new Vertex(curr.x - 1, curr.y, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 1) {
				if (!wallcollide(curr.x - 1, curr.y)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x - 1, curr.y)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				Vertex k = new Vertex(curr.x, curr.y + 1, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 2) {
				if (!wallcollide(curr.x, curr.y + 1)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y + 1) && wallcollide(curr.x + 1, curr.y)
						&& wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x + 1, curr.y) && wallcollide(curr.x, curr.y + 1)) {
					Vertex k = new Vertex(curr.x, curr.y - 1, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				Vertex k = new Vertex(curr.x + 1, curr.y, curr);
				k.head = curr.head % 4;
				return k;
			} else if (curr.head == 3) {
				if (!wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x + 1, curr.y, curr);
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
				if (wallcollide(curr.x, curr.y - 1) && wallcollide(curr.x - 1, curr.y)
						&& wallcollide(curr.x + 1, curr.y)) {
					Vertex k = new Vertex(curr.x, curr.y + 1, curr);
					k.head = (curr.head + 2) % 4;
					return k;
				}
				if (wallcollide(curr.x + 1, curr.y) && wallcollide(curr.x, curr.y - 1)) {
					Vertex k = new Vertex(curr.x - 1, curr.y, curr);
					k.head = (curr.head + 1) % 4;
					return k;
				}
				Vertex k = new Vertex(curr.x, curr.y - 1, curr);
				k.head = curr.head % 4;
				return k;
			} else {
				return curr;
			}
		}
	}

	private Vertex wallFollower(Vertex curr) {
		curr.head = 0;
		while (this.map[curr.x][curr.y] != -3) {
			if (!keepSolving)
				return null;
			this.map[curr.x][curr.y] = -1;
			curr = move(curr, 1);
			notifyObservers();
			delay();
		}
		if (this.map[curr.x][curr.y] == -3) {
			this.found = true;
			return curr;
		} else
			return null;
	}

	int counter = 0;

	private Vertex turn(Vertex curr) {
		Vertex k;
		if (this.counter == 0 && curr.head == 0 && !wallcollide(curr.x - 1, curr.y)) {
			k = new Vertex(curr.x - 1, curr.y, curr);
			return k;
		}
		if (curr.head == 0) {
			if (wallcollide(curr.x - 1, curr.y)) {
				if (!wallcollide(curr.x, curr.y + 1)) {
					k = new Vertex(curr.x, curr.y + 1, curr);
					this.counter++;
					k.head = (curr.head + 1) % 4;
					return k;
				} else {
					if (wallcollide(curr.x, curr.y - 1)) {
						curr.head = (curr.head - 1 + 4) % 4;
						this.counter--;
						return curr;
					}
					k = new Vertex(curr.x, curr.y - 1, curr);
					this.counter--;
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
			} else if (!wallcollide(curr.x, curr.y + 1)) {
				k = new Vertex(curr.x, curr.y + 1, curr);
				this.counter++;
				k.head = (curr.head + 1) % 4;
				return k;
			} else {
				k = new Vertex(curr.x - 1, curr.y, curr);
				k.head = curr.head;
				return k;
			}
		} else if (curr.head == 1) {
			if (wallcollide(curr.x, curr.y + 1)) {
				if (!wallcollide(curr.x + 1, curr.y)) {
					k = new Vertex(curr.x + 1, curr.y, curr);
					this.counter++;
					k.head = (curr.head + 1) % 4;
					return k;
				} else {
					if (wallcollide(curr.x - 1, curr.y)) {
						curr.head = (curr.head - 1 + 4) % 4;
						this.counter--;
						return curr;
					}
					k = new Vertex(curr.x - 1, curr.y, curr);
					this.counter--;
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
			} else if (!wallcollide(curr.x + 1, curr.y)) {
				k = new Vertex(curr.x + 1, curr.y, curr);
				this.counter++;
				k.head = (curr.head + 1) % 4;
				return k;
			} else {
				k = new Vertex(curr.x, curr.y + 1, curr);
				k.head = curr.head;
				return k;
			}
		} else if (curr.head == 2) {
			if (wallcollide(curr.x + 1, curr.y)) {
				if (!wallcollide(curr.x, curr.y - 1)) {
					k = new Vertex(curr.x, curr.y - 1, curr);
					this.counter++;
					k.head = (curr.head + 1) % 4;
					return k;
				} else {
					if (wallcollide(curr.x, curr.y + 1)) {
						curr.head = (curr.head - 1 + 4) % 4;
						this.counter--;
						return curr;
					}
					k = new Vertex(curr.x, curr.y + 1, curr);
					this.counter--;
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
			} else if (!wallcollide(curr.x, curr.y - 1)) {
				k = new Vertex(curr.x, curr.y - 1, curr);
				this.counter++;
				k.head = (curr.head + 1) % 4;
				return k;
			} else {
				k = new Vertex(curr.x + 1, curr.y, curr);
				k.head = curr.head;
				return k;
			}
		} else {
			if (wallcollide(curr.x, curr.y - 1)) {
				if (!wallcollide(curr.x - 1, curr.y)) {
					k = new Vertex(curr.x - 1, curr.y, curr);
					this.counter++;
					k.head = (curr.head + 1) % 4;
					return k;
				} else {
					if (wallcollide(curr.x + 1, curr.y)) {
						curr.head = (curr.head - 1 + 4) % 4;
						this.counter--;
						return curr;
					}
					k = new Vertex(curr.x + 1, curr.y, curr);
					this.counter--;
					k.head = (curr.head - 1 + 4) % 4;
					return k;
				}
			} else if (!wallcollide(curr.x - 1, curr.y)) {
				k = new Vertex(curr.x - 1, curr.y, curr);
				this.counter++;
				k.head = (curr.head + 1) % 4;
				return k;
			} else {
				k = new Vertex(curr.x, curr.y - 1, curr);
				k.head = curr.head;
				return k;
			}
		}
	}

	private Vertex pledge(Vertex curr) {
		curr.head = 0;
		while (this.map[curr.x][curr.y] != -3) {
			if (!keepSolving)
				return null;
			this.map[curr.x][curr.y] = -1;
			curr = turn(curr);
			notifyObservers();
			delay();
		}
		if (this.map[curr.x][curr.y] == -3) {
			this.found = true;
			return curr;
		}
		return null;
	}

	private Vertex move2(Vertex curr) {
		if (this.line.size() == 1)
			return this.line.remove(0);
		this.line.remove(curr);
		int nextidx = this.line.indexOf(curr) + 1;
		if (nextidx < this.line.size() && !wallcollide(this.line.get(nextidx).x, this.line.get(nextidx).y)) {
			Vertex next = new Vertex(this.line.get(nextidx).x, this.line.get(nextidx).y, curr);

			return next;
		} else {
			Vertex robot1 = move(curr, 1);
			robot1.prev = curr;
			Vertex robot2 = move(curr, -1);
			robot2.prev = curr;
			boolean moveRobot1 = true;
			boolean moveRobot2 = true;
			while (moveRobot1 || moveRobot2) {
				if (!keepSolving)
					return null;
				if (this.line.contains(robot1) || (robot1.equals(curr) && robot1.head == curr.head)) {
					moveRobot1 = false;
				}
				if (this.line.contains(robot2) || (robot1.equals(curr) && robot1.head == curr.head)) {
					moveRobot2 = false;
				}
				if (moveRobot1) {
					Vertex prev = robot1;
					this.map[robot1.x][robot1.y] = -1;
					robot1 = move(robot1, 1);
					robot1.prev = prev;
				}
				if (moveRobot2) {
					Vertex prev2 = robot2;
					this.map[robot2.x][robot2.y] = -1;
					robot2 = move(robot2, -1);
					robot2.prev = prev2;
				}
				notifyObservers();
				delay();
			}
			Vertex end = this.line.get(this.line.size() - 1);
			if ((this.line.contains(robot1) && this.line.contains(robot2))) {
				return (Math.sqrt(Math.pow(robot2.x - end.x, 2) + Math.pow(robot2.y - end.y, 2)) < Math
						.sqrt(Math.pow(robot1.x - end.x, 2) + Math.pow(robot1.y - end.y, 2))) ? robot2 : robot1;
			}
			if (this.line.contains(robot1))
				return robot1;
			if (this.line.contains(robot2))
				return robot2;
			return null;
		}
	}

	List<Vertex> line = new ArrayList<Vertex>();

	private Vertex chain(Vertex start, Vertex end) {
		int i = start.x, j = start.y;
		boolean row = true;
		while (i != end.x || j != end.y) {

			Vertex k = new Vertex(i, j, null);
			if (!this.line.contains(k))
				this.line.add(k);
			if (row) {
				if (i < end.x)
					i++;
				else if (i > end.x)
					i--;
			} else {
				if (j < end.y)
					j++;
				else if (j > end.y)
					j--;
			}
			row = !row;
		}
		this.line.add(end);
		start.head = 0;
		while (start.x != end.x || start.y != end.y) {
			if (!keepSolving)
				return null;
			start = move2(start);
			if (start == null)
				return null;
			this.map[start.x][start.y] = -1;
			line = line.subList(line.indexOf(start), line.size());
		}
		if (start.x == end.x && start.y == end.y) {
			this.found = true;
			return start;
		}
		return null;
	}

	private HashMap<Vertex, Integer> surrondings(Vertex curr) {
		HashMap<Vertex, Integer> surr = new HashMap<Vertex, Integer>();
		if (!wallcollide(curr.x + 1, curr.y))
			surr.put(new Vertex(curr.x + 1, curr.y, curr), this.map[curr.x + 1][curr.y]);
		if (!wallcollide(curr.x - 1, curr.y))
			surr.put(new Vertex(curr.x - 1, curr.y, curr), this.map[curr.x - 1][curr.y]);
		if (!wallcollide(curr.x, curr.y + 1))
			surr.put(new Vertex(curr.x, curr.y + 1, curr), this.map[curr.x][curr.y + 1]);
		if (!wallcollide(curr.x, curr.y - 1))
			surr.put(new Vertex(curr.x, curr.y - 1, curr), this.map[curr.x][curr.y - 1]);
		return surr;
	}

	private Vertex movet(Vertex curr) {
		HashMap<Vertex, Integer> surr = surrondings(curr);
		if (surr.isEmpty())
			return null;
		List<Vertex> zeroVertices = new ArrayList<>();
		List<Vertex> minusOneVertices = new ArrayList<>();
		Vertex minusTwoVertex = null;
		for (Map.Entry<Vertex, Integer> entry : surr.entrySet()) {
			if (entry.getValue() == -3) {
				return entry.getKey();
			} else if (entry.getValue() == 0) {
				zeroVertices.add(entry.getKey());
			} else if (entry.getValue() == -1) {
				minusOneVertices.add(entry.getKey());
			} else if (entry.getValue() == -2) {
				minusTwoVertex = entry.getKey();
			}
		}
		Random rand = new Random();
		if (!zeroVertices.isEmpty()) {
			int r = rand.nextInt(zeroVertices.size());
			return zeroVertices.get(r);
		} else if (!minusOneVertices.isEmpty()) {
			int r = rand.nextInt(minusOneVertices.size());
			return minusOneVertices.get(r);
		} else {
			return minusTwoVertex;
		}
	}

	private boolean wallcollide2(int sx, int sy) {
		if ((sx >= 0 && sx < this.map.length) && (sy >= 0 && sy < this.map[0].length) && (this.map[sx][sy] != 1))
			return false;
		return true;
	}

	private int cellType(Vertex curr) {
		int count = 0;
		if (!wallcollide2(curr.x + 1, curr.y))
			count++;
		if (!wallcollide2(curr.x - 1, curr.y))
			count++;
		if (!wallcollide2(curr.x, curr.y + 1))
			count++;
		if (!wallcollide2(curr.x, curr.y - 1))
			count++;
		if (count >= 3)
			return 0;
		else if (count == 2)
			return 1;
		return 2;
	}

	private Vertex tremaux(Vertex curr) {
		while (this.map[curr.x][curr.y] != -3) {
			if (!keepSolving)
				return null;
			if (this.map[curr.x][curr.y] == 0)
				this.map[curr.x][curr.y] = -1;
			else if (this.map[curr.x][curr.y] == -1 && cellType(curr) != 0)
				this.map[curr.x][curr.y] = -6;
			int type = cellType(curr);
			if (type == 2)
				this.map[curr.x][curr.y] = -6;
			else if (type == 0) {
				HashMap<Vertex, Integer> surr = surrondings(curr);
				int notMinusSixCount = 0;
				for (Map.Entry<Vertex, Integer> entry : surr.entrySet()) {
					if (entry.getValue() != -6) {
						notMinusSixCount++;
					}
				}
				if (notMinusSixCount == 1) {
					this.map[curr.x][curr.y] = -6;
				}
			}
			curr = movet(curr);
			notifyObservers();
			delay();
		}
		this.found = true;
		return curr;
	}

	private void filldeadEnd(Vertex curr) {
		while (cellType(curr) == 2 && this.map[curr.x][curr.y] != -3 && this.map[curr.x][curr.y] != -2) {
			if (!keepSolving)
				return;
			this.map[curr.x][curr.y] = 1;
			if (!wallcollide(curr.x + 1, curr.y))
				curr = new Vertex(curr.x + 1, curr.y, null);
			else if (!wallcollide(curr.x - 1, curr.y))
				curr = new Vertex(curr.x - 1, curr.y, null);
			else if (!wallcollide(curr.x, curr.y + 1))
				curr = new Vertex(curr.x, curr.y + 1, null);
			else if (!wallcollide(curr.x, curr.y - 1))
				curr = new Vertex(curr.x, curr.y - 1, null);
			notifyObservers();
			delay();
		}
	}

	private Vertex deadend(Vertex curr) {
		for (int i = 1; i < this.map.length - 1; i++) {
			for (int j = 1; j < this.map[0].length - 1; j++) {
				if (!keepSolving)
					return null;
				if (this.map[i][j] == -2 || this.map[i][j] == -3)
					continue;
				if (cellType(new Vertex(i, j, null)) == 2 && this.map[i][j] != 1)
					filldeadEnd(new Vertex(i, j, null));
			}
		}
		DFS(curr);
		return this.location;
	}

}
// djikstra for weighted graphs
/*
 * Djikstra HashMap<Vertex, ArrayList<Vertex>> list = new HashMap<Vertex,
 * ArrayList<Vertex>>(); HashMap<Vertex, Integer> table = new HashMap<Vertex,
 * Integer>(); HashSet<Vertex> unvisited = new HashSet<Vertex>(); private void
 * addNeighbor(int r, int c, int y, int x) { Vertex k = new Vertex(r, c, null);
 * Vertex neighbor = new Vertex(r + y, c + x, null); if
 * (this.unvisited.contains(neighbor)) return; this.list.get(k).add(neighbor);
 * this.list.get(neighbor).add(k); this.table.put(k, Integer.MAX_VALUE);
 * this.table.put(neighbor, Integer.MAX_VALUE); }
 * 
 * private void addVertices() { for (int i = 0; i < this.map.length; i++) for
 * (int j = 0; j < this.map[0].length; j++) { if (!collide(i, j))
 * this.list.put(new Vertex(i, j, null), new ArrayList<Vertex>()); } for (int i
 * = 0; i < this.map.length; i++) { for (int j = 0; j < this.map[0].length; j++)
 * { if (collide(i, j)) continue; if (!collide(i + 1, j)) { addNeighbor(i, j, 1,
 * 0); } if (!collide(i - 1, j)) { addNeighbor(i, j, -1, 0); } if (!collide(i, j
 * + 1)) { addNeighbor(i, j, 0, 1); } if (!collide(i, j - 1)) { addNeighbor(i,
 * j, 0, -1); } this.unvisited.add(new Vertex(i, j, null)); } } }
 * 
 * PriorityQueue<Vertex> pq = new PriorityQueue<Vertex>(new Comparator<Vertex>()
 * {
 * 
 * @Override public int compare(Vertex v1, Vertex v2) { return
 * Integer.compare(table.get(v1), table.get(v2)); } });
 * 
 * private Vertex djikstra(Vertex curr) { while (!pq.isEmpty()) { if
 * (!keepSolving) return null; curr = pq.poll(); if (this.map[curr.x][curr.y] ==
 * -3) { this.found = true; return curr; } if (!this.unvisited.contains(curr))
 * continue; this.map[curr.x][curr.y] = -1; for (Vertex neighbor :
 * this.list.get(curr)) { if (this.table.get(curr) + 1 <
 * this.table.get(neighbor)) { this.table.put(neighbor, this.table.get(curr) +
 * 1); } neighbor.prev = curr; pq.add(neighbor); } this.unvisited.remove(curr);
 * notifyObservers(); delay(50); } return curr; } }
 */
// cul-de-sac for braided mazes
/*
 * private void miniDFS(Vertex curr) { Vertex x; if (cellType(curr) == 0) {
 * this.map[curr.x][curr.y] = 1; return; } if (!wallcollide(curr.x + 1, curr.y))
 * { x = new Vertex(curr.x + 1, curr.y, curr); miniDFS(x);
 * this.map[curr.x][curr.y] = 1; } if (!wallcollide(curr.x - 1, curr.y)) { x =
 * new Vertex(curr.x - 1, curr.y, curr); miniDFS(x); this.map[curr.x][curr.y] =
 * 1; } if (!wallcollide(curr.x, curr.y + 1)) { x = new Vertex(curr.x, curr.y +
 * 1, curr); miniDFS(x); this.map[curr.x][curr.y] = 1; } if
 * (!wallcollide(curr.x, curr.y - 1)) { x = new Vertex(curr.x, curr.y - 1,
 * curr); miniDFS(x); this.map[curr.x][curr.y] = 1; } }
 * 
 * private boolean notStraight(Vertex curr) { if (cellType(curr) != 1) return
 * false; boolean top = false; boolean right = false; boolean left = false;
 * boolean down = false; if (!wallcollide(curr.x + 1, curr.y)) down = true; if
 * (!wallcollide(curr.x - 1, curr.y)) top = true; if (!wallcollide(curr.x,
 * curr.y + 1)) right = true; if (!wallcollide(curr.x, curr.y - 1)) left = true;
 * if (top == down || left == right) return false; return true; } private Vertex
 * culdesac(Vertex curr) { for (int i = 1; i < this.map.length - 1; i++) { for
 * (int j = 1; j < this.map[0].length - 1; j++) { if (!wallcollide(i, j) &&
 * notStraight(new Vertex(i, j, null))) { miniDFS(new Vertex(i, j, null)); } } }
 * return deadend(curr); }
 */
// blind alley for finding all solutions in a maze
/*
 * for each junction in the maze, send a robot (wall following useing move
 * function) in each passage of the junction (make sure to change their head so
 * if a junction has passage up down right change the first robot head to 0
 * second to 2 third to 1) if the robots return to the junction and the path it
 * returned from doesnt contain start or end cells then fill the entire path
 * with walls, after doing this for each junction the remaining structure will
 * only consist of all the paths linking the start cell to the end cell (useless
 * in perfect maze, just use dead-end filler for same effect)
 */
