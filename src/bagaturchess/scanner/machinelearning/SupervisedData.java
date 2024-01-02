package bagaturchess.scanner.machinelearning;


import bagaturchess.scanner.tests.Test;


public class SupervisedData {
	
	
	/**
	 * Training sets
	 */
	private static final SupervisedData gen_data_book_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_1_extended/",
			new String[] {
					"./res/cnn/books/set1/input1.png",
					"./res/cnn/books/set1/input2.png",
					"./res/cnn/books/set1/input3.png",
					"./res/cnn/books/set1/input4.png",
					"./res/cnn/books/set1/input5.png",
					"./res/cnn/books/set1/input6.png",
					"./res/cnn/books/set1/input7.png",
					"./res/cnn/books/set1/input8.png",
					"./res/cnn/books/set1/input9.png",
					"./res/cnn/books/set1/input10.png",
					"./res/cnn/books/set1/input11.png",
					"./res/cnn/books/set1/input12.png",
					"./res/cnn/books/set1/input13.png",
					"./res/cnn/books/set1/input14.png",
					"./res/cnn/books/set1/input15.png",
					"./res/cnn/books/set1/input16.png",
					"./res/cnn/books/set1/input17.png",
					"./res/cnn/books/set1/input18.png",
					"./res/cnn/books/set1/input19.png",
					"./res/cnn/books/set1/input20.png",
					"./res/cnn/books/set1/input21.png",
					"./res/cnn/books/set1/input22.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	private static final SupervisedData gen_data_book_set2 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_2_extended/",
			new String[] {
					"./res/cnn/books/set2/input1.png",
					"./res/cnn/books/set2/input2.png",
					"./res/cnn/books/set2/input3.png",
					"./res/cnn/books/set2/input4.png",
					"./res/cnn/books/set2/input5.png",
					"./res/cnn/books/set2/input6.png",
					"./res/cnn/books/set2/input7.png",
					"./res/cnn/books/set2/input8.png",
					"./res/cnn/books/set2/input9.png",
					"./res/cnn/books/set2/input10.png",
					"./res/cnn/books/set2/input11.png",
					"./res/cnn/books/set2/input12.png",
					"./res/cnn/books/set2/input13.png",
					"./res/cnn/books/set2/input14.png",
					"./res/cnn/books/set2/input15.png",
					"./res/cnn/books/set2/input16.png",
					"./res/cnn/books/set2/input17.png",
					"./res/cnn/books/set2/input18.png",
					"./res/cnn/books/set2/input19.png",
					"./res/cnn/books/set2/input20.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"3Q2nr/5qpp/2k5/8/8/8/2B3PP/6K1 w - - 0 1",
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
					"3Q2nr/5qpp/2k5/8/8/8/2B3PP/6K1 w - - 0 1",
					"3Q2nr/5qpp/2k5/8/8/8/2B3PP/6K1 w - - 0 1",
					"3Q2nr/5qpp/2k5/8/8/8/2B3PP/6K1 w - - 0 1",
					Test.INITIAL_FEN,
					"3Q2nr/5qpp/2k5/8/8/8/2B3PP/6K1 w - - 0 1",
					"r5k1/1b3N2/8/8/6n1/1R4B1/6K1/8 w - - 0 1",
			}
		);
	
	
	private static final SupervisedData gen_data_book_set3 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_3_extended/",
			new String[] {
					"./res/cnn/books/set3/input1.png",
					"./res/cnn/books/set3/input2.png",
					"./res/cnn/books/set3/input3.png",
					"./res/cnn/books/set3/input4.png",
					"./res/cnn/books/set3/input5.png",
					"./res/cnn/books/set3/input6.png",
					"./res/cnn/books/set3/input7.png",
			},
			new String[] {
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w KQkq - 0 1",
			}
		);
	
	
	private static final SupervisedData gen_data_book_set4 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_4_extended/",
			new String[] {
					"./res/cnn/books/set4/input1.png",
					"./res/cnn/books/set4/input2.png",
					"./res/cnn/books/set4/input3.jpg",
			},
			new String[] {
					"2r2rk1/p2n1ppp/1qb1pn2/1p4B1/3P4/P2B1P2/1P2N1PP/R2Q1RK1 w - - 0 1",
					"2r2rk1/p2n1ppp/1qb1pn2/1p4B1/3P4/P2B1P2/1P2N1PP/R2Q1RK1 w - - 0 1",
					"2r1r1k1/p5pb/qpQ2n1p/2bpN3/5B2/1PP2N1P/P2R1PP1/5RK1 w - - 0 1",
			}
		);
	
	
	private static final SupervisedData gen_data_chesscom_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_chesscom_set_1_extended/",
			new String[] {
					"./res/cnn/chess.com/set1/input1.png",
					"./res/cnn/chess.com/set1/input2.png",
					"./res/cnn/chess.com/set1/input3.png",
					"./res/cnn/chess.com/set1/input4.png",
					"./res/cnn/chess.com/set1/input5.png",
					"./res/cnn/chess.com/set1/input6.png",
					"./res/cnn/chess.com/set1/input7.png",
					"./res/cnn/chess.com/set1/input8.png",
					"./res/cnn/chess.com/set1/input9.png",
					"./res/cnn/chess.com/set1/input10.png",
					"./res/cnn/chess.com/set1/input11.png",
					"./res/cnn/chess.com/set1/input12.png",
					"./res/cnn/chess.com/set1/input13.png",
					"./res/cnn/chess.com/set1/input14.png",
					"./res/cnn/chess.com/set1/input15.png",
					"./res/cnn/chess.com/set1/input16.png",
					"./res/cnn/chess.com/set1/input17.png",
					"./res/cnn/chess.com/set1/input18.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	private static final SupervisedData gen_data_chesscom_set2 = new SupervisedData(
			"./datasets_deepnetts/dataset_chesscom_set_2_extended/",
			new String[] {
					"./res/cnn/chess.com/set2/input1.png",
			},
			new String[] {
					"RNBKQBNR/PPP1PPPP/8/3P4/5p2/8/ppppp1pp/rnbkqbnr w KQkq - 0 1",
			}
		);
	
	
	private static final SupervisedData gen_data_chess24com_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_chess24com_set_1_extended/",
			new String[] {
					"./res/cnn/chess24.com/set1/input1.png",
					"./res/cnn/chess24.com/set1/input2.png",
					"./res/cnn/chess24.com/set1/input3.png",
					"./res/cnn/chess24.com/set1/input4.png",
					"./res/cnn/chess24.com/set1/input5.png",
					"./res/cnn/chess24.com/set1/input6.png",
					"./res/cnn/chess24.com/set1/input7.png",
					"./res/cnn/chess24.com/set1/input8.png",
					"./res/cnn/chess24.com/set1/input9.png",
					"./res/cnn/chess24.com/set1/input10.png",
					"./res/cnn/chess24.com/set1/input11.png",
					"./res/cnn/chess24.com/set1/input12.png",
					"./res/cnn/chess24.com/set1/input13.png",
					"./res/cnn/chess24.com/set1/input14.png",
					"./res/cnn/chess24.com/set1/input15.png",
					"./res/cnn/chess24.com/set1/input16.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	private static final SupervisedData gen_data_lichessorg_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
			new String[] {
					"./res/cnn/lichess.org/set1/input1.png",
					"./res/cnn/lichess.org/set1/input2.png",
					"./res/cnn/lichess.org/set1/input3.png",
					"./res/cnn/lichess.org/set1/input4.png",
					"./res/cnn/lichess.org/set1/input5.png",
					"./res/cnn/lichess.org/set1/input6.png",
					"./res/cnn/lichess.org/set1/input7.png",
					"./res/cnn/lichess.org/set1/input8.png",
					"./res/cnn/lichess.org/set1/input9.png",
					"./res/cnn/lichess.org/set1/input10.png",
					"./res/cnn/lichess.org/set1/input11.png",
					"./res/cnn/lichess.org/set1/input12.png",
					"./res/cnn/lichess.org/set1/input13.png",
					"./res/cnn/lichess.org/set1/input14.png",
					"./res/cnn/lichess.org/set1/input15.png",
					"./res/cnn/lichess.org/set1/input16.png",
					"./res/cnn/lichess.org/set1/input17.png",
					"./res/cnn/lichess.org/set1/input18.png",
					"./res/cnn/lichess.org/set1/input19.png",
					"./res/cnn/lichess.org/set1/input20.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	public static final SupervisedData[] source_set_all = new SupervisedData[] {
			gen_data_book_set1,
			gen_data_book_set2,
			gen_data_book_set3,
			gen_data_book_set4,
			gen_data_chesscom_set1,
			gen_data_chesscom_set2,
			gen_data_chess24com_set1,
			gen_data_lichessorg_set1
		};
	
	
	/**
	 * Test sets
	 */
	private static final SupervisedData test_data_book_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_1_extended/",
			new String[] {
					"./res/tests/books/set1/test1.png",
					"./res/tests/books/set1/test1.png",
					"./res/tests/books/set1/test3.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	private static final SupervisedData test_data_book_set2 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_2_extended/",
			new String[] {

			},
			new String[] {

			}
		);
	
	
	private static final SupervisedData test_data_book_set3 = new SupervisedData(
			"./datasets_deepnetts/dataset_books_set_3_extended/",
			new String[] {

			},
			new String[] {

			}
		);
	
	
	private static final SupervisedData test_data_chesscom_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_chesscom_set_1_extended/",
			new String[] {
					"./res/tests/chess.com/set1/test1.png",
					"./res/tests/chess.com/set1/test2.png",
					"./res/tests/chess.com/set1/test3.png",
					"./res/tests/chess.com/set1/test4.png",
			},
			new String[] {
					"r3kn1r/2q1bp2/p1pp1n1p/1P2p1p1/P1B1P3/2N1BNPb/2P1QP2/R2R2K1 w - - 0 1",
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	private static final SupervisedData test_data_chesscom_set2 = new SupervisedData(
			"./datasets_deepnetts/dataset_chesscom_set_2_extended/",
			new String[] {

			},
			new String[] {

			}
		);
	
	
	private static final SupervisedData test_data_chess24com_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_chess24com_set_1_extended/",
			new String[] {

			},
			new String[] {
					
			}
		);
	
	
	private static final SupervisedData test_data_lichessorg_set1 = new SupervisedData(
			"./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
			new String[] {
					"./res/tests/lichess.org/set1/test1.png",
					"./res/tests/lichess.org/set1/test2.png",
					"./res/tests/lichess.org/set1/test3.png",
					"./res/tests/lichess.org/set1/test4.png",
					"./res/tests/lichess.org/set1/test5.png",
					"./res/tests/lichess.org/set1/test6.png",
					"./res/tests/lichess.org/set1/test7.png",
					"./res/tests/lichess.org/set1/test8.png",
					"./res/tests/lichess.org/set1/test9.png",
					"./res/tests/lichess.org/set1/test10.png",
					"./res/tests/lichess.org/set1/test11.png",
			},
			new String[] {
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					"r1b1kb1r/pp1q1ppp/1npp1n2/3Pp3/4P3/1BN2N2/PPP2PPP/R1BQK2R w - - 0 1",
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
					Test.INITIAL_FEN,
			}
		);
	
	
	public static final SupervisedData[] test_set_all = new SupervisedData[] {
		test_data_book_set1,
		test_data_book_set2,
		test_data_book_set3,
		test_data_chesscom_set1,
		test_data_chesscom_set2,
		test_data_chess24com_set1,
		test_data_lichessorg_set1,
	};
	
	
	public String dataset_dir;
	
	public String[] input_files;
	
	public String[] fens;
	
	public Object[] props;
	
	
	public SupervisedData(String _output_dir, String[] _input_files, String[] _fens) {
		
		this(_output_dir, _input_files, _fens, new Object[_input_files.length]);
	}
	
	
	public SupervisedData(String _output_dir, String[] _input_files, String[] _fens, Object[] _props) {
		dataset_dir = _output_dir;
		input_files = _input_files;
		fens = _fens;
		props = _props;
	}
}
