package com.isvan.toys.heightMapPort;

import com.isvan.toys.heightMapPort.dataStructures.Options;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Options o = new Options();

		o.mountains = 5;
		o.lakes = 0;
		o.width = 500;
		o.height = 500;
		o.threads = 8;
		o.fileName = "../outPut.png";
		o.seed = 7437833873965204480l;
		o.seed = (long) (Math.random() * Long.MAX_VALUE);

		if (false) {
			switch (args.length) {

			case 0:
				System.out
						.println("You didnt inputanything, do 'help' for input commands");
				System.exit(0);
				break;

			case 1:
				if (args[0].equalsIgnoreCase("help")) {
					System.out.println("Input properties are : ");
					System.out
							.println("outputfile width height threads mountains lakes [seed]");
					System.exit(0);
				}
				break;

			case 6:
				o.fileName = args[0];
				o.width = Integer.parseInt(args[1]);
				o.height = Integer.parseInt(args[2]);
				o.threads = Integer.parseInt(args[3]);
				o.mountains = Integer.parseInt(args[4]);
				o.lakes = Integer.parseInt(args[5]);

				break;

			case 7:

				o.fileName = args[0];
				o.width = Integer.parseInt(args[1]);
				o.height = Integer.parseInt(args[2]);
				o.threads = Integer.parseInt(args[3]);
				o.mountains = Integer.parseInt(args[4]);
				o.lakes = Integer.parseInt(args[5]);
				o.seed = Long.parseLong(args[6]);
				break;

			default:
				System.out.println("Unknown input do 'help' for info");
				System.exit(0);

			}
		}

		System.out.println("Seed used : " + o.seed);
		HeightMap m = new HeightMap(o);
		m.generateMap();

	}
}
