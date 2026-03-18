//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "gramatica.y"
import java.io.*;
import java.util.Stack;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
//#line 27 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short IF=257;
public final static short THEN=258;
public final static short ELSE=259;
public final static short BEGIN=260;
public final static short END=261;
public final static short END_IF=262;
public final static short OUTF=263;
public final static short TYPEDEF=264;
public final static short FUN=265;
public final static short RET=266;
public final static short STRING=267;
public final static short REPEAT=268;
public final static short WHILE=269;
public final static short GOTO=270;
public final static short ID=271;
public final static short LONGINT=272;
public final static short HEXA=273;
public final static short CML=274;
public final static short DOUBLE=275;
public final static short TOD=276;
public final static short STRUCT=277;
public final static short ASIGNACION=278;
public final static short DISTINTO=279;
public final static short MENOR_IGUAL=280;
public final static short MAYOR_IGUAL=281;
public final static short ETIQUETA=282;
public final static short LOWER_THAN_ELSE=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    0,    1,    1,    2,    2,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,   10,    7,    7,    7,    7,    7,
    7,    7,    7,   12,   12,    4,    4,    4,    4,    4,
    4,    4,    4,    4,    4,    4,    4,   16,   16,   16,
   16,   16,   16,   16,   19,   15,   15,   15,   15,   15,
   15,   15,   15,   11,   11,   17,   17,   20,   22,   22,
   14,   14,   14,   14,   14,   14,   14,   14,   14,   14,
   14,   14,   14,   14,   14,   14,   14,   14,   23,   23,
   24,   24,   21,   21,   13,   18,   18,   18,   28,   18,
   18,   18,   27,   27,   27,   27,   27,   27,   27,   27,
   27,   27,    8,    8,    8,    8,    8,    8,    8,    8,
    8,    6,    6,    6,    6,   30,   29,   31,   26,   26,
    9,    9,    9,    5,    5,    5,    5,   25,   25,   25,
   25,   25,   25,
};
final static short yylen[] = {                            2,
    4,    3,    2,    3,    2,    2,    2,    1,    1,    1,
    3,    2,    1,   10,    9,    9,    8,    7,    9,    9,
   11,    9,   10,   10,    1,    9,    8,    8,    7,    8,
    8,    9,    9,    2,    1,    1,    1,    1,    1,    2,
    3,    2,    2,    1,    1,    5,    3,    7,    6,    6,
    6,    6,    5,    6,    1,    5,    5,    4,    4,    4,
    3,    3,    5,    2,    1,    4,    3,    3,    2,    1,
    8,   10,    7,    7,    8,    7,    7,    5,    9,    9,
    9,    9,    6,    7,    7,    9,    9,    8,    1,    1,
    1,    1,    3,    3,    4,    3,    3,    4,    0,    5,
    3,    1,    3,    3,    4,    4,    4,    4,    2,    2,
    4,    1,    1,    1,    1,    1,    2,    1,    2,    1,
    2,    3,    3,    1,    1,    3,    3,    1,    3,    1,
    3,    2,    1,    1,    1,    1,    2,    1,    1,    1,
    1,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    3,    0,    0,    0,   55,    0,
    0,  135,   45,  134,   44,    0,    8,    9,   10,    0,
    0,   13,   36,   37,   38,   39,    0,    0,  124,    0,
    0,    0,    0,  116,  118,  120,    0,    0,    0,    0,
    0,  112,  113,    0,    0,    0,  115,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   43,    0,    0,
    7,    0,    0,    0,    0,    0,    0,   40,    0,    0,
    0,    0,    2,    0,   90,   89,    0,    0,    0,  117,
  119,  121,  110,  109,    0,    0,    0,  142,  141,  140,
    0,    0,  138,  139,  143,    0,    0,    0,    0,    0,
   61,    0,    0,    0,    0,   62,    0,    0,  136,    0,
  133,    0,    0,    0,    0,   47,   41,  126,   67,    0,
   25,    0,    0,   11,    0,    0,    0,    0,  123,   70,
    0,    0,    0,    1,    0,  127,  101,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  103,    0,    0,  104,    0,    0,   58,    0,
    0,    0,  137,    0,    0,    0,  132,    0,    0,    0,
   66,    0,    0,    0,    0,    0,   95,  128,    0,   68,
   69,    0,    0,    0,   78,  111,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  105,  106,  108,  107,
   63,   56,   57,    0,    0,    0,    0,  131,    0,    0,
    0,    0,   46,   64,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   53,    0,    0,    0,    0,    0,    0,
    0,    0,  100,   92,   91,    0,   83,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   35,    0,    0,   54,   50,   52,    0,   51,   74,    0,
   84,    0,    0,   76,    0,    0,    0,   73,    0,    0,
   85,    0,   18,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   29,   34,    0,   48,    0,   75,    0,
    0,    0,   71,    0,   88,    0,    0,    0,   17,    0,
    0,    0,    0,    0,   28,   30,    0,    0,   31,   27,
   86,   82,   81,    0,   79,   87,   20,   15,    0,   16,
    0,   22,   19,    0,   33,   32,   26,   72,   14,    0,
   24,   23,   21,
};
final static short yydgoto[] = {                          3,
   16,  251,   18,   19,   20,   21,   22,   42,  112,   65,
  173,  252,   23,   24,   25,   26,   43,   44,   28,   76,
   45,  131,   77,  236,   96,  127,   46,  233,   47,   29,
  179,
};
final static short yysindex[] = {                      -135,
  737,  757,    0,  521,    0,  527,   -8,  533,    0,  -14,
   36,    0,    0,    0,    0,  939,    0,    0,    0, -188,
  -35,    0,    0,    0,    0,    0,  -42, -160,    0,  939,
  777,  989,  110,    0,    0,    0,   41,   82,  435,  435,
  559,    0,    0,  102,  -34,   86,    0,  -22,    2,   27,
 -177,  324, -120, -120,  384,   89,   76,    0, -113,  566,
    0,  -27,  121,   25,  128,  384,  -98,    0,  -59,  134,
  -69,  799,    0,   36,    0,    0,  -54,  -68,  573,    0,
    0,    0,    0,    0,  -48,    9,  384,    0,    0,    0,
  487,  586,    0,    0,    0,  384,  989,  -44,  592,  600,
    0,  179,  181,  172,  116,    0,  -80,  -18,    0, -120,
    0,  -30,  166,  424,  247,    0,    0,    0,    0,  282,
    0, -120,  319,    0, -120,  137,   53,  121,    0,    0,
 1004,  384,  539,    0,   10,    0,    0,  302,  989,  989,
  959,  137,  232,  408,   86,  408,   86,  137, -202,  989,
  435,  435,    0,  435,  435,    0,  228,  234,    0,  237,
  435,  399,    0,  426, -120,   20,    0,  435,  265,  258,
    0,   47,  290,  275,   77,  295,    0,    0,  384,    0,
    0,  297,  580,   21,    0,    0,  101,  -63,  722,  108,
  384,   86,   86,  989,  292,  -43,    0,    0,    0,    0,
    0,    0,    0,  328,  607,  330,  338,    0,  121,  350,
  352,   20,    0,    0,  138,  142,  364,  939,  146,  137,
  348,  349,   26,    0,  359,  361,  989,  363,  168,  989,
  663,  373,    0,    0,    0,  180,    0,  989,  387,  435,
  395,  412,  435,   20,  -39,  435,  422,  939,  939,  819,
    0,  839,  939,    0,    0,    0,  415,    0,    0,  217,
    0,  439,  219,    0,  974,  449,  250,    0,  454,  252,
    0,  401,    0,  435,   -3,  475,  253,  438,  -15,  859,
  879,  693,  899,    0,    0,  919,    0,  484,    0,  485,
  486,  286,    0,  490,    0,  496,  501,    6,    0,  503,
   -9,  505,  506,  299,    0,    0,  310,  -85,    0,    0,
    0,    0,    0,  517,    0,    0,    0,    0,  518,    0,
  -53,    0,    0,  523,    0,    0,    0,    0,    0,  526,
    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -32,    0,    0,    0,    0,  587,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  589,    0,  -20,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  456,    0,    0,    0,    0,
 -111,    0,    0,    0,    0,    0,   38,    0,    0,    0,
    0,    0,   58,  221,    0,    0,    0,    0,    0,    0,
    0,  593,    0,  -24,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   83,    0,   78,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -23,    0,    0,  461,    0,  466,    7,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  105,    0,  129,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  -33,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  492,  497,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  548,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   87,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  153,    0,    0,    0,
    0,    0,    0,    0,    0,  177,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  201,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,
};
final static short yygindex[] = {                         0,
   44,    3,    0,  753,    1,   59,    0,  665,  -19,  535,
  -64, -161,    0,    0,    0,    0,   -1,  875,    0,  -25,
  -13,    0,  -46, -124,    0,    0,  -28,    0,    0,  -51,
    0,
};
final static int YYTABLESIZE=1286;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         27,
   27,  277,   71,   17,   17,  331,   98,   65,   67,  166,
  161,  125,  122,  165,   27,  129,   68,   94,   61,  125,
  114,  114,  114,  114,  114,  304,  114,   86,   27,   27,
   27,  321,   17,   61,  114,   94,  101,   42,  114,  114,
  114,  114,  104,   39,   58,   31,   38,   93,   40,  141,
  149,   54,  111,  113,  111,  299,  194,  125,  175,  195,
  176,  225,  145,  147,  318,   93,  257,   27,   67,   91,
   27,   92,  114,   72,   61,   60,   62,  122,   64,  224,
   79,   59,   63,  124,  256,  106,  280,  281,  283,  300,
  164,  286,  187,  188,  190,   27,  178,   25,  319,   69,
  107,  125,  260,  196,   60,  263,  267,  162,   70,  217,
  111,  177,  167,  270,  167,  192,  125,  193,  182,  184,
  308,  122,  172,  172,    1,  172,  130,   99,   59,   27,
  129,   91,  100,   92,  117,    2,  122,   27,   27,   27,
  292,  130,  231,  108,   91,  129,   92,  116,   27,   60,
  109,   12,   49,  137,   14,   78,  160,  118,   91,  137,
   92,   93,   95,   94,  167,  208,   59,  125,  235,  223,
  326,    4,  128,  132,  172,  327,   77,    6,    7,   91,
    8,   92,    9,  108,   10,   11,   12,   27,   13,   14,
  109,   12,   27,  129,   14,  227,   15,    4,  228,  133,
   80,  235,  136,    6,  235,  235,    8,  135,    9,  139,
   10,   74,  235,  150,   13,  238,   27,  330,  239,  157,
   12,  158,   15,   97,  210,   27,   65,  129,   27,   27,
  159,  128,  136,  108,   94,  114,   27,  114,  136,  235,
  109,   12,   66,  121,   14,  125,   27,   27,   27,  129,
   27,   27,  163,  125,  285,  128,  168,  102,  114,  114,
  114,  128,   51,   27,   93,  185,  140,   57,   52,   53,
  247,  191,   33,   34,   35,  103,   36,   37,   27,   27,
   27,   27,  285,  285,   27,  285,  201,  170,  285,   91,
  209,   92,  202,   42,   42,  203,   42,   42,   42,   42,
   42,   42,  276,   42,  212,   42,   27,   42,   42,   42,
  285,   42,   42,  125,  125,  216,  213,  214,  125,   42,
  125,  125,  171,  125,   91,  125,   92,  125,  125,  125,
  215,  125,  125,  122,  122,  219,  218,  221,  122,  125,
  122,  122,  186,  122,   91,  122,   92,  122,  122,  122,
  237,  122,  122,   80,   81,  122,   82,   87,  174,  122,
   60,   60,  226,   60,   60,   60,   60,   60,   60,  232,
   60,  240,   60,  243,   60,   60,   60,  244,   60,   60,
   88,   89,   90,  110,   59,   59,   60,   59,   59,   59,
   59,   59,   59,  245,   59,  246,   59,  248,   59,   59,
   59,  249,   59,   59,  250,  253,  254,  255,   49,   49,
   59,   49,   49,   49,   49,   49,   49,  258,   49,  259,
   49,  261,   49,   49,   49,   39,   49,   49,   38,  262,
   40,  268,   77,   77,   49,   77,   77,   77,   77,   77,
   77,  269,   77,   38,   77,  271,   77,   77,   77,   39,
   77,   77,   38,  273,   40,  274,   80,   80,   77,   80,
   80,   80,   80,   80,   80,  279,   80,  165,   80,  165,
   80,   80,   80,  287,   80,   80,   12,   12,  288,   38,
  290,   12,   80,   12,   12,  169,   12,  207,   12,  205,
   12,   12,   12,  297,   12,   12,  102,  289,  102,  102,
  102,   96,   12,   96,   96,   96,   97,  293,   97,   97,
   97,  294,  295,  296,  102,  102,  102,  102,  301,   96,
   96,   96,   96,  302,   97,   97,   97,   97,   39,  144,
  303,   38,   98,   40,   98,   98,   98,   99,  108,   99,
   99,   99,  311,  312,  313,  109,   12,  314,  315,   14,
   98,   98,   98,   98,  316,   99,   99,   99,   99,  317,
   41,  320,   39,  322,  323,   38,   49,   40,   39,  324,
  325,   38,   55,   40,   39,  328,  329,   38,  183,   40,
   39,  332,  108,   38,  333,   40,    6,  108,    5,  109,
   12,  125,    4,   14,  109,   12,  123,    0,   14,   85,
   39,    0,    0,   38,    0,   40,  119,   39,    0,    0,
   38,    0,   40,  137,   39,    0,    0,   38,    0,   40,
  222,   39,    0,    0,   38,    0,   40,   39,  146,    0,
   38,    0,   40,  151,    0,    0,   38,    0,  152,    0,
    0,  154,    0,    0,   38,    0,  155,    0,    0,    0,
    0,   38,    0,    0,   33,   34,   35,    0,   36,   37,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   33,
   34,   35,    0,   36,    0,    0,    0,    0,   33,   34,
   35,    0,   36,  143,    0,    0,    0,  108,    0,  108,
    0,    0,    0,    0,  109,   12,  109,   12,   14,  241,
   14,    0,    0,   83,   84,   33,   34,   35,    0,   36,
    0,  102,    0,  102,    0,    0,   96,    0,   96,    0,
    0,   97,    0,   97,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  102,  102,  102,    0,    0,   96,
   96,   96,    0,    0,   97,   97,   97,   98,    0,   98,
    0,    0,   99,    0,   99,    0,    0,   33,   34,   35,
    0,   36,  143,  153,  156,    0,    0,    0,    0,    0,
   98,   98,   98,    0,    0,   99,   99,   99,   32,    0,
    0,    0,    0,    0,   75,    0,    0,    0,    0,    0,
    0,   33,   34,   35,    0,   36,   37,   33,   34,   35,
   48,   36,   37,   33,   34,   35,    0,   36,   37,   33,
   34,   35,    0,   36,   37,  197,  198,    0,  199,  200,
    0,  130,    0,    0,    0,  204,  206,    0,    0,   33,
   34,   35,  211,   36,   37,    0,   33,   34,   35,    0,
   36,   37,    0,   33,   34,   35,    0,   36,   37,   75,
   33,   34,   35,    0,   36,   37,   33,   34,   35,    0,
   36,  143,   33,   34,   35,    0,   36,    0,    0,  242,
   33,   34,   35,    0,   36,    0,    0,   33,   34,   35,
   50,   36,   56,  181,    0,    0,    0,    0,    0,    0,
    0,   75,   75,   75,    0,    0,    0,    0,    0,    0,
    0,    0,   75,    0,  272,    0,    0,  275,    0,    0,
  278,    0,    0,    0,    0,    0,    0,    0,  264,    4,
    0,  265,   69,  105,  266,    6,    0,    0,    8,  115,
    9,    0,   10,   74,  120,    0,   13,    0,  298,    0,
  126,   75,    0,    0,   15,    0,  234,    0,  307,    4,
    0,    0,    0,  138,    0,    6,    7,    0,    8,    0,
    9,  142,   10,   11,   12,    0,   13,   14,    0,    0,
  148,    0,    0,    0,   15,    0,    0,  229,    4,  234,
  230,   69,  234,  234,    6,    0,    0,    8,    0,    9,
  234,   10,   74,    4,    0,   13,    0,    5,    0,    6,
    7,    0,    8,   15,    9,    0,   10,   11,   12,    0,
   13,   14,    0,    4,    0,    0,   30,  234,   15,    6,
    7,    0,    8,    0,    9,    0,   10,   11,   12,    0,
   13,   14,    0,    4,    0,    0,    0,   73,   15,    6,
    7,    0,    8,    0,    9,    0,   10,   11,   12,    0,
   13,   14,    0,  220,    0,    4,    0,    0,   15,  134,
    0,    6,    7,    0,    8,  138,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,  282,    0,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,  284,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,  305,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,  306,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,  309,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,  310,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,    0,    0,    0,    0,
   15,    6,    7,    0,    8,    0,    9,    0,   10,   11,
   12,    0,   13,   14,    0,    4,  189,    0,   69,    0,
   15,    6,    0,    0,    8,    0,    9,    0,   10,   74,
    4,    0,   13,   69,    0,  291,    6,    0,    0,    8,
   15,    9,    0,   10,   74,    4,    0,   13,   69,    0,
    0,    6,    0,    0,    8,   15,    9,    0,   10,   74,
    4,    0,   13,    0,  180,    0,    6,    0,    0,    8,
   15,    9,    0,   10,   74,    0,    0,   13,    0,    0,
    0,    0,    0,    0,    0,   15,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          1,
    2,   41,   28,    1,    2,   59,   41,   41,   44,   40,
   91,   44,   40,   44,   16,   67,   59,   41,   16,   44,
   41,   42,   43,   44,   45,   41,   47,   41,   30,   31,
   32,   41,   30,   31,   54,   59,   59,    0,   59,   60,
   61,   62,   41,   42,   59,    2,   45,   41,   47,   41,
   97,   60,   52,   53,   54,   59,  259,    0,  123,  262,
  125,   41,   91,   92,   59,   59,   41,   69,   44,   43,
   72,   45,   93,   30,   72,   40,  265,    0,   20,   59,
   40,   46,  271,   59,   59,   59,  248,  249,  250,   93,
  110,  253,  139,  140,  141,   97,   44,   40,   93,  260,
  278,   44,  227,  150,    0,  230,  231,  107,  269,  174,
  110,   59,  112,  238,  114,  144,   59,  146,  132,  133,
  282,   44,  122,  123,  260,  125,   44,   42,    0,  131,
   44,   43,   47,   45,   59,  271,   59,  139,  140,  141,
  265,   59,  189,  264,   43,   59,   45,   59,  150,   40,
  271,  272,    0,  265,  275,   46,   41,  271,   43,  271,
   45,   60,   61,   62,  164,  165,   46,   40,  194,  183,
  256,  257,  271,   40,  174,  261,    0,  263,  264,   43,
  266,   45,  268,  264,  270,  271,  272,  189,  274,  275,
  271,  272,  194,  245,  275,  259,  282,  257,  262,  269,
    0,  227,  271,  263,  230,  231,  266,  262,  268,  258,
  270,  271,  238,  258,  274,  259,  218,  271,  262,   41,
    0,   41,  282,  258,  166,  227,  260,  279,  230,  231,
   59,  271,  265,  264,  258,  256,  238,  258,  271,  265,
  271,  272,  278,  271,  275,  278,  248,  249,  250,  301,
  252,  253,  271,  278,  252,  271,   91,  256,  279,  280,
  281,  271,  271,  265,  258,  256,  258,  282,  277,  278,
  212,   40,  271,  272,  273,  274,  275,  276,  280,  281,
  282,  283,  280,  281,  286,  283,   59,   41,  286,   43,
  271,   45,   59,  256,  257,   59,  259,  260,  261,  262,
  263,  264,  244,  266,   40,  268,  308,  270,  271,  272,
  308,  274,  275,  256,  257,   41,   59,  271,  261,  282,
  263,  264,   41,  266,   43,  268,   45,  270,  271,  272,
   41,  274,  275,  256,  257,   41,  260,   41,  261,  282,
  263,  264,   41,  266,   43,  268,   45,  270,  271,  272,
   59,  274,  275,  272,  273,  278,  275,  256,   40,  282,
  256,  257,  262,  259,  260,  261,  262,  263,  264,  262,
  266,   44,  268,   44,  270,  271,  272,   40,  274,  275,
  279,  280,  281,   60,  256,  257,  282,  259,  260,  261,
  262,  263,  264,   44,  266,   44,  268,  260,  270,  271,
  272,  260,  274,  275,   41,  260,   59,   59,  256,  257,
  282,  259,  260,  261,  262,  263,  264,   59,  266,   59,
  268,   59,  270,  271,  272,   42,  274,  275,   45,  262,
   47,   59,  256,  257,  282,  259,  260,  261,  262,  263,
  264,  262,  266,   45,  268,   59,  270,  271,  272,   42,
  274,  275,   45,   59,   47,   44,  256,  257,  282,  259,
  260,  261,  262,  263,  264,   44,  266,   44,  268,   44,
  270,  271,  272,   59,  274,  275,  256,  257,  262,   45,
  262,  261,  282,  263,  264,   62,  266,   62,  268,   91,
  270,  271,  272,   93,  274,  275,   41,   59,   43,   44,
   45,   41,  282,   43,   44,   45,   41,   59,   43,   44,
   45,  262,   59,  262,   59,   60,   61,   62,   44,   59,
   60,   61,   62,  271,   59,   60,   61,   62,   42,   43,
   93,   45,   41,   47,   43,   44,   45,   41,  264,   43,
   44,   45,   59,   59,   59,  271,  272,  262,   59,  275,
   59,   60,   61,   62,   59,   59,   60,   61,   62,   59,
   40,   59,   42,   59,   59,   45,   40,   47,   42,  271,
  261,   45,   40,   47,   42,   59,   59,   45,   40,   47,
   42,   59,  264,   45,   59,   47,    0,  264,    0,  271,
  272,   44,    0,  275,  271,  272,   62,   -1,  275,   41,
   42,   -1,   -1,   45,   -1,   47,   41,   42,   -1,   -1,
   45,   -1,   47,   41,   42,   -1,   -1,   45,   -1,   47,
   41,   42,   -1,   -1,   45,   -1,   47,   42,   43,   -1,
   45,   -1,   47,   42,   -1,   -1,   45,   -1,   47,   -1,
   -1,   42,   -1,   -1,   45,   -1,   47,   -1,   -1,   -1,
   -1,   45,   -1,   -1,  271,  272,  273,   -1,  275,  276,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  271,
  272,  273,   -1,  275,   -1,   -1,   -1,   -1,  271,  272,
  273,   -1,  275,  276,   -1,   -1,   -1,  264,   -1,  264,
   -1,   -1,   -1,   -1,  271,  272,  271,  272,  275,   93,
  275,   -1,   -1,   39,   40,  271,  272,  273,   -1,  275,
   -1,  256,   -1,  258,   -1,   -1,  256,   -1,  258,   -1,
   -1,  256,   -1,  258,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  279,  280,  281,   -1,   -1,  279,
  280,  281,   -1,   -1,  279,  280,  281,  256,   -1,  258,
   -1,   -1,  256,   -1,  258,   -1,   -1,  271,  272,  273,
   -1,  275,  276,   99,  100,   -1,   -1,   -1,   -1,   -1,
  279,  280,  281,   -1,   -1,  279,  280,  281,  258,   -1,
   -1,   -1,   -1,   -1,   32,   -1,   -1,   -1,   -1,   -1,
   -1,  271,  272,  273,   -1,  275,  276,  271,  272,  273,
  274,  275,  276,  271,  272,  273,   -1,  275,  276,  271,
  272,  273,   -1,  275,  276,  151,  152,   -1,  154,  155,
   -1,   69,   -1,   -1,   -1,  161,  162,   -1,   -1,  271,
  272,  273,  168,  275,  276,   -1,  271,  272,  273,   -1,
  275,  276,   -1,  271,  272,  273,   -1,  275,  276,   97,
  271,  272,  273,   -1,  275,  276,  271,  272,  273,   -1,
  275,  276,  271,  272,  273,   -1,  275,   -1,   -1,  205,
  271,  272,  273,   -1,  275,   -1,   -1,  271,  272,  273,
    6,  275,    8,  131,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  139,  140,  141,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  150,   -1,  240,   -1,   -1,  243,   -1,   -1,
  246,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,
   -1,  259,  260,   49,  262,  263,   -1,   -1,  266,   55,
  268,   -1,  270,  271,   60,   -1,  274,   -1,  274,   -1,
   66,  189,   -1,   -1,  282,   -1,  194,   -1,  256,  257,
   -1,   -1,   -1,   79,   -1,  263,  264,   -1,  266,   -1,
  268,   87,  270,  271,  272,   -1,  274,  275,   -1,   -1,
   96,   -1,   -1,   -1,  282,   -1,   -1,  256,  257,  227,
  259,  260,  230,  231,  263,   -1,   -1,  266,   -1,  268,
  238,  270,  271,  257,   -1,  274,   -1,  261,   -1,  263,
  264,   -1,  266,  282,  268,   -1,  270,  271,  272,   -1,
  274,  275,   -1,  257,   -1,   -1,  260,  265,  282,  263,
  264,   -1,  266,   -1,  268,   -1,  270,  271,  272,   -1,
  274,  275,   -1,  257,   -1,   -1,   -1,  261,  282,  263,
  264,   -1,  266,   -1,  268,   -1,  270,  271,  272,   -1,
  274,  275,   -1,  179,   -1,  257,   -1,   -1,  282,  261,
   -1,  263,  264,   -1,  266,  191,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,  260,   -1,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,  261,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,  261,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,  261,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,  261,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,  261,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,   -1,   -1,   -1,   -1,
  282,  263,  264,   -1,  266,   -1,  268,   -1,  270,  271,
  272,   -1,  274,  275,   -1,  257,  258,   -1,  260,   -1,
  282,  263,   -1,   -1,  266,   -1,  268,   -1,  270,  271,
  257,   -1,  274,  260,   -1,  262,  263,   -1,   -1,  266,
  282,  268,   -1,  270,  271,  257,   -1,  274,  260,   -1,
   -1,  263,   -1,   -1,  266,  282,  268,   -1,  270,  271,
  257,   -1,  274,   -1,  261,   -1,  263,   -1,   -1,  266,
  282,  268,   -1,  270,  271,   -1,   -1,  274,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  282,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=283;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"IF","THEN","ELSE","BEGIN","END","END_IF",
"OUTF","TYPEDEF","FUN","RET","STRING","REPEAT","WHILE","GOTO","ID","LONGINT",
"HEXA","CML","DOUBLE","TOD","STRUCT","ASIGNACION","DISTINTO","MENOR_IGUAL",
"MAYOR_IGUAL","ETIQUETA","LOWER_THAN_ELSE",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID BEGIN cuerpo END",
"prog : ID cuerpo END",
"prog : BEGIN END",
"prog : ID BEGIN cuerpo",
"prog : ID cuerpo",
"prog : BEGIN cuerpo",
"cuerpo : cuerpo sentencia",
"cuerpo : sentencia",
"sentencia : sentencia_declaracion",
"sentencia : sentencia_ejecucion",
"sentencia_declaracion : tipo lista_variables ';'",
"sentencia_declaracion : tipo lista_variables",
"sentencia_declaracion : declaracion_funcion",
"sentencia_declaracion : TYPEDEF ID ASIGNACION tipo '[' factor ',' factor ']' ';'",
"sentencia_declaracion : TYPEDEF ID ASIGNACION tipo '[' factor ',' factor ';'",
"sentencia_declaracion : TYPEDEF ID ASIGNACION tipo factor ',' factor ']' ';'",
"sentencia_declaracion : TYPEDEF ID ASIGNACION tipo factor ',' factor ';'",
"sentencia_declaracion : TYPEDEF ID ASIGNACION tipo '[' ']' ';'",
"sentencia_declaracion : TYPEDEF ASIGNACION tipo '[' factor ',' factor ']' ';'",
"sentencia_declaracion : TYPEDEF ID ASIGNACION '[' factor ',' factor ']' ';'",
"sentencia_declaracion : TYPEDEF STRUCT '<' lista_tipos '>' '(' lista_variables ',' ')' ID ';'",
"sentencia_declaracion : TYPEDEF STRUCT lista_tipos '(' lista_variables ',' ')' ID ';'",
"sentencia_declaracion : TYPEDEF '<' lista_tipos '>' '(' lista_variables ',' ')' ID ';'",
"sentencia_declaracion : TYPEDEF STRUCT '<' lista_tipos '>' '(' lista_variables ',' ')' ';'",
"actualizar_ambito : ID",
"declaracion_funcion : tipo FUN actualizar_ambito '(' parametro ')' BEGIN cuerpo_funcion END",
"declaracion_funcion : tipo actualizar_ambito '(' parametro ')' BEGIN cuerpo_funcion END",
"declaracion_funcion : tipo FUN '(' parametro ')' BEGIN cuerpo_funcion END",
"declaracion_funcion : tipo FUN actualizar_ambito parametro BEGIN cuerpo_funcion END",
"declaracion_funcion : tipo FUN actualizar_ambito '(' ')' BEGIN cuerpo_funcion END",
"declaracion_funcion : tipo FUN actualizar_ambito '(' parametro ')' cuerpo_funcion END",
"declaracion_funcion : tipo FUN actualizar_ambito '(' parametro ')' BEGIN cuerpo_funcion error",
"declaracion_funcion : tipo FUN actualizar_ambito '(' parametro ')' BEGIN error END",
"cuerpo_funcion : cuerpo_funcion sentencia",
"cuerpo_funcion : sentencia",
"sentencia_ejecucion : asignacion",
"sentencia_ejecucion : condicion_if",
"sentencia_ejecucion : sentencia_print",
"sentencia_ejecucion : sentencia_while",
"sentencia_ejecucion : invocacion_funcion ';'",
"sentencia_ejecucion : GOTO ETIQUETA ';'",
"sentencia_ejecucion : GOTO ETIQUETA",
"sentencia_ejecucion : GOTO ';'",
"sentencia_ejecucion : ETIQUETA",
"sentencia_ejecucion : CML",
"sentencia_ejecucion : RET '(' expresion ')' ';'",
"sentencia_ejecucion : RET expresion ';'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE '(' condicion ')' ';'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE '(' condicion ')'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE '(' ')' ';'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE condicion ')' ';'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE '(' condicion ';'",
"sentencia_while : repeat bloque_sentencia_ejecutable WHILE condicion ';'",
"sentencia_while : repeat WHILE '(' condicion ')' ';'",
"repeat : REPEAT",
"sentencia_print : OUTF '(' CML ')' ';'",
"sentencia_print : OUTF '(' expresion ')' ';'",
"sentencia_print : OUTF '(' ')' ';'",
"sentencia_print : OUTF '(' expresion ')'",
"sentencia_print : OUTF '(' CML ')'",
"sentencia_print : OUTF CML ';'",
"sentencia_print : OUTF expresion ';'",
"sentencia_print : OUTF '(' error ')' ';'",
"parametro : tipo ID",
"parametro : tipo",
"invocacion_funcion : ID '(' expresion ')'",
"invocacion_funcion : ID '(' ')'",
"bloque_sentencia_ejecutable : BEGIN lista_sentencias END",
"lista_sentencias : lista_sentencias sentencia_ejecucion",
"lista_sentencias : sentencia_ejecucion",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then END_IF ';'",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then ELSE cuerpo_else END_IF ';'",
"condicion_if : IF '(' condicion ')' cuerpo_then END_IF ';'",
"condicion_if : IF '(' ')' THEN cuerpo_then END_IF ';'",
"condicion_if : IF '(' condicion ')' THEN error END_IF ';'",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then error",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then END_IF",
"condicion_if : IF THEN cuerpo_then END_IF error",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then cuerpo_else END_IF ';'",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then ELSE cuerpo_else END_IF",
"condicion_if : IF '(' condicion ')' THEN cuerpo_then ELSE END_IF ';'",
"condicion_if : IF '(' condicion ')' THEN ELSE cuerpo_else END_IF ';'",
"condicion_if : IF condicion THEN cuerpo_then END_IF ';'",
"condicion_if : IF '(' condicion THEN cuerpo_then END_IF ';'",
"condicion_if : IF condicion ')' THEN cuerpo_then END_IF ';'",
"condicion_if : IF '(' condicion THEN cuerpo_then ELSE cuerpo_else END_IF ';'",
"condicion_if : IF condicion ')' THEN cuerpo_then ELSE cuerpo_else END_IF ';'",
"condicion_if : IF condicion THEN cuerpo_then ELSE cuerpo_else END_IF ';'",
"cuerpo_then : bloque_sentencia_ejecutable",
"cuerpo_then : sentencia_ejecucion",
"cuerpo_else : bloque_sentencia_ejecutable",
"cuerpo_else : sentencia_ejecucion",
"condicion : expresion comparador expresion",
"condicion : expresion error expresion",
"asignacion : lista_variables ASIGNACION lista_expresiones ';'",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"expresion : expresion '+' '+' termino",
"$$1 :",
"expresion : expresion '-' '+' termino $$1",
"expresion : TOD '(' ')'",
"expresion : termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : termino '*' '*' factor",
"termino : termino '*' '/' factor",
"termino : termino '/' '/' factor",
"termino : termino '/' '*' factor",
"termino : '/' factor",
"termino : '*' factor",
"termino : TOD '(' expresion ')'",
"termino : factor",
"factor : invocacion_funcion",
"factor : ID",
"factor : id_compuesta_exp",
"factor : LONGINT",
"factor : '-' LONGINT",
"factor : HEXA",
"factor : '-' HEXA",
"factor : DOUBLE",
"factor : '-' DOUBLE",
"lista_variables : lista_variables ',' ID",
"lista_variables : lista_variables ',' id_compuesta_var",
"lista_variables : id_compuesta_var",
"lista_variables : ID",
"id_compuesta_var : ID '.' ID",
"id_compuesta_exp : ID '.' ID",
"coma : ','",
"lista_expresiones : lista_expresiones coma expresion",
"lista_expresiones : expresion",
"lista_tipos : lista_tipos ',' tipo",
"lista_tipos : lista_tipos tipo",
"lista_tipos : tipo",
"tipo : DOUBLE",
"tipo : LONGINT",
"tipo : ID",
"tipo : TYPEDEF ID",
"comparador : '<'",
"comparador : '>'",
"comparador : MAYOR_IGUAL",
"comparador : MENOR_IGUAL",
"comparador : DISTINTO",
"comparador : '='",
};

//#line 913 "gramatica.y"
void yyerror(String mensaje) {
  error = true;
  // funcion utilizada para imprimir errores que produce yacc
  System.out.println( mensaje );
  errores.add(mensaje);
}

  void yywarning(String mensaje) {
    // funcion utilizada para imprimir errores que produce yacc
    System.out.println( mensaje );
    warnings.add(mensaje);
  }

String ambitoActual = "main";
public static ArrayList<String> listaVariablesTemp = new ArrayList<>();
public static ArrayList<String> listaTipos = new ArrayList<>();
public static ArrayList<String> tiposValidos = new ArrayList<>();
public static ArrayList<String> expresionTemp = new ArrayList<>();
public static ArrayList<ArrayList<String>> polacaFunciones = new ArrayList<>();
public static ArrayList<String> errores = new ArrayList<>();
public static ArrayList<String> warnings = new ArrayList<>();
public static boolean esUnaFuncion = false;
public static boolean esNuevaFuncion = false;
public static boolean esAsignacionMultiple = false;
public static boolean tipoId = false;
AnalizadorLexico lector;
public static ArrayList<String> polaca = new ArrayList<>();
public static ArrayList<String> posicionDePolaca = new ArrayList<>();
public static Stack<Integer> pila = new Stack<>(); //para bifuraciones en while/if algun otro
public static Stack<Integer> pilaFuncion = new Stack<>();// para bifurcaciones en if/while pero en funciones??
public static Stack<Integer> pilaIndiceFuncion = new Stack<>();// para guardar las posicion donde van los returns osea el indice de la funcion
public static Integer indiceFuncionActual=0;
public static Set<Integer> funcionesConRetorno = new HashSet<>();
// La clave es el nombre de la etiqueta y el valor es una lista de posiciones pendientes.
Map<String, ArrayList<int[]>> saltosPendientes = new HashMap<>();
public static boolean error = false;
public void pushPila(int valor) {
    if (esUnaFuncion) {
        pilaFuncion.push(valor);
    } else {
        pila.push(valor);
    }
}
public int popPila() {
    if (esUnaFuncion) {
        return pilaFuncion.pop();
    } else {
        return pila.pop();
    }
}

public int obtenerPosicionPolaca() {
    return polaca.size();
}
public void verificarEtiquetas() {
    for (String etiqueta : saltosPendientes.keySet()) {
        yyerror("ERROR: La etiqueta " + etiqueta + " no está definida en el código.");
    }
}
public static void agregarTokenPolaca(String lexema){
	 if (esUnaFuncion) {

                polacaFunciones.get(indiceFuncionActual).add(lexema);
         } else{

		polaca.add(lexema);
		}
}

public void imprimirPolacaFunciones() {
  if (!polacaFunciones.isEmpty()) {
    System.out.println();
    System.out.println("-------- IMPRIMIENDO CODIGO INTERMEDIO FUNCIONES (POLACA) --------");
    for (int i = 0; i < polacaFunciones.size(); i++) {
      System.out.println("Funcion: " + i);
      ArrayList<String> expresionFuncion = polacaFunciones.get(i);
      imprimir(expresionFuncion);
      System.out.println("---- Fin funcion " + i + " ----");
      System.out.println();
    }
    System.out.println("-------- FIN CODIGO INTERMEDIO FUNCIONES--------");
    System.out.println(); // Nueva línea al final de cada función
  }
}

public static void eliminarTokenPolaka() {
  polaca.remove(polaca.size() - 1);
}
public static void agregarCheckpoint(){
	if (esUnaFuncion)
	{
		pilaFuncion.push(polacaFunciones.get(indiceFuncionActual).size());
	}
	else
		pila.push(polaca.size()); //guardamos el checkpoint en caso de una difurcacion
}
int yylex(){
    return lector.yylex();
}

public static void imprimirPolaca() {
    // Imprimo la polaca inversa generada en el programa
    if (!polaca.isEmpty()) {
        System.out.println();
        System.out.println("------------ IMPRIMIENDO CODIGO INTERMEDIO (POLACA) ------------");

        for (int i = 0; i < polaca.size(); ++i) {
                System.out.println(i + " " + polaca.get(i));
        }
        System.out.println("-------- FIN CODIGO INTERMEDIO --------");
    }
}
public int obtenerPosicionPolacaEnFuncion(int indiceFuncion) {
    if (indiceFuncion < 0 || indiceFuncion >= polacaFunciones.size()) {
        yyerror("ERROR: El índice de función es inválido.");
        return -1;
    }
    ArrayList<String> polacaDeFuncion = polacaFunciones.get(indiceFuncion);
    return polacaDeFuncion.size();
}
public void agregarTokenPolacaEnFuncion( String token, int indiceFuncion) {
    if (indiceFuncion < 0 || indiceFuncion >= polacaFunciones.size()) {
        yyerror("ERROR: El índice de función es inválido.");
        return;
    }
    ArrayList<String> polacaDeFuncion = polacaFunciones.get(indiceFuncion);

    // Agregamos el token a la lista de instrucciones de la función
    polacaDeFuncion.add(token);
}
public static void imprimir(ArrayList<String> lista){
	for (int i = 0; i< lista.size();i++){
	    System.out.println(i + " " + lista.get(i));

	}
}

public void cargarVariable (String lexema, String tipo, String uso, String estructura,String limSup, String limInf, String tipoParametro, String nombreParametro, ArrayList<String> variables, Integer token){
  DatosTablaSimbolos datos = lector.tablaSimbolos.getDato(lexema); //chequear, se pisan los datos
  String auxLexema = lexema + "@" + ambitoActual;
  if(!lector.tablaSimbolos.existeLexema(auxLexema)) {
    DatosTablaSimbolos d;
    if(datos != null)
      d = new DatosTablaSimbolos(datos.getToken());
    else
      d = new DatosTablaSimbolos(token);
    lector.tablaSimbolos.borrarLexema(lexema);
    d.setTipo(tipo);
    d.setUso(uso);
    d.setAmbito(auxLexema);
    d.setEstructura(estructura);
    d.setLimites(limSup, limInf);
    d.setTipoParametro(tipoParametro);
    d.setNombreParametro(nombreParametro);
    d.setListaVar(variables);
    lector.tablaSimbolos.addToken(auxLexema,d);
  }
  else{
    if (uso.equals("Nombre de Funcion"))
        yyerror("ERROR: se esta redeclarando la funcion: " + lexema + " en la linea " + lector.getNroLinea());
    if (uso.equals("Nombre de variable"))
        yyerror("ERROR: se esta redeclarando la variable: " + lexema + " en la linea " + lector.getNroLinea());
    if (uso.equals("Nombre de tipo"))
        yyerror("ERROR: se esta redeclarando el tipo: " + lexema + " en la linea " + lector.getNroLinea());
  }
}

/*
 DatosTablaSimbolos dato1 = lector.tablaSimbolos.getDato(variable);
 DatosTablaSimbolos dato2 = lector.tablaSimbolos.getDato(expresion);
 String tipoVariable = dato1.getTipo();
 String tipoExpresion = dato2.getTipo();
 if (!tipoVariable.equals(tipoExpresion)) {
 yyerror("ERROR: incompatibilidad de tipos entre " + variable + " y " + expresion + " en la línea " + lector.getNroLinea());
 }
 */


public boolean tieneAlcance(String var){
  String auxAmbito = var + "@" + ambitoActual;
  DatosTablaSimbolos dato = null;

  while(!auxAmbito.equals(var)) {
    if(lector.tablaSimbolos.existeLexema(auxAmbito))
        return true;
    auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
  }
    return false;
}
public String buscarNombreParametro(String funcion){
  String auxAmbito = funcion + "@" + ambitoActual;
  DatosTablaSimbolos dato = null;

  while(!auxAmbito.equals(funcion)) {
    if(lector.tablaSimbolos.existeLexema(auxAmbito)) {
      DatosTablaSimbolos datoFuncion = lector.tablaSimbolos.getDato(auxAmbito);
      return datoFuncion.getNombreParametro();
    }
    auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
  }
  return null;
}

public String verAlcance(String var){
      String auxAmbito = var + "@" + ambitoActual;
      DatosTablaSimbolos dato = null;

    while(!auxAmbito.equals(var)) {
      if(lector.tablaSimbolos.existeLexema(auxAmbito))
        return auxAmbito;
      auxAmbito = auxAmbito.substring(0, auxAmbito.lastIndexOf("@"));
    }
  return null;
}
//#line 935 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 2:
//#line 26 "gramatica.y"
{yyerror("ERROR, falta begin programa principal en la linea: " + lector.getNroLinea());}
break;
case 3:
//#line 27 "gramatica.y"
{yyerror("ERROR, falta el ID del programa principal en la linea: " + lector.getNroLinea());}
break;
case 4:
//#line 28 "gramatica.y"
{yyerror("ERROR, falta END del programa principal en la linea: " + lector.getNroLinea());}
break;
case 5:
//#line 29 "gramatica.y"
{yyerror("ERROR, falta BEGIN,END del programa principal en la linea: " + lector.getNroLinea());}
break;
case 6:
//#line 30 "gramatica.y"
{yyerror("ERROR, falta ID,END del programa principal en la linea: " + lector.getNroLinea());}
break;
case 11:
//#line 42 "gramatica.y"
{

                                               for (String var : listaVariablesTemp) {
                                                   var = var.toUpperCase();
                                                   if(tipoId){
                                                      String padre = verAlcance(val_peek(2).sval.toUpperCase());

                                                      if(padre != null){
                                                          DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(padre);
                                                          String tipoPadre = dato.getTipo();
                                                          ArrayList<String> variables = dato.getListaVar();
                                                          ArrayList<String> variablesAux = new ArrayList<String>();
                                                          if(tipoPadre.equals("struct")){
                                                            for(String list: variables){
                                                                String varAux = verAlcance(list);
                                                                DatosTablaSimbolos datoVar = lector.tablaSimbolos.getDato(varAux);

                                                                cargarVariable(var+"_"+list, datoVar.getTipo(), "Nombre de variable", null, null, null, null, null, null,datoVar.getToken());

                                                                variablesAux.add(var+"_"+list);
                                                            }

                                                            cargarVariable(var, tipoPadre, "Nombre de variable", null, null, null, null, null, variablesAux,null);
                                                          }
                                                          else{
                                                               cargarVariable(var, val_peek(2).sval.toUpperCase(), "Nombre de variable", tipoPadre, null, null, null, null, null, null);
                                                               lector.tablaSimbolos.borrarLexema(var);
                                                          }
                                                      }
                                                   }
                                                   else{
                                                   if (val_peek(2).sval.equalsIgnoreCase("DOUBLE") || val_peek(2).sval.equalsIgnoreCase("LONGINT"))
                                                       cargarVariable(var, val_peek(2).sval.toUpperCase(), "Nombre de variable", null, null, null, null, null,null, null);
                                                   lector.tablaSimbolos.borrarLexema(var);
                                                   }
                                               }
                                               tipoId = false;
                                               esAsignacionMultiple = false;
                                               listaVariablesTemp.clear();
                                        }
break;
case 12:
//#line 82 "gramatica.y"
{yyerror("ERROR, Falta ; en la sentencia de declaracion en la linea: " + lector.getNroLinea());}
break;
case 14:
//#line 85 "gramatica.y"
{											cargarVariable(val_peek(8).sval, val_peek(6).sval, "Nombre de tipo", null,val_peek(2).sval,val_peek(4).sval, null, null,null, null);
						    			tiposValidos.add(val_peek(6).sval);
						    			}
break;
case 15:
//#line 88 "gramatica.y"
{yyerror("ERROR, Falta de ']' en la linea: " + lector.getNroLinea());}
break;
case 16:
//#line 89 "gramatica.y"
{yyerror("ERROR, Falta de '[' en la linea: " + lector.getNroLinea());}
break;
case 17:
//#line 90 "gramatica.y"
{yyerror("ERROR, Falta de llaves '[]' en la linea: " + lector.getNroLinea());}
break;
case 18:
//#line 91 "gramatica.y"
{yyerror("ERROR, Falta de rango en la linea: " + lector.getNroLinea());}
break;
case 19:
//#line 92 "gramatica.y"
{yyerror("ERROR, Falta nombre del tipo definido en la linea: " + lector.getNroLinea());}
break;
case 20:
//#line 93 "gramatica.y"
{yyerror("ERROR, Falta el tipo base en la linea: " + lector.getNroLinea());}
break;
case 21:
//#line 94 "gramatica.y"
{ArrayList<String> variables = new ArrayList<>();
                                                                                                         if (listaTipos.size() == listaVariablesTemp.size()) {


                                                                                                                     for (int i = 0; i < listaTipos.size(); i++) {
                                                                                                                         String tipo = listaTipos.get(i);
                                                                                                                         String variable = listaVariablesTemp.get(i);
                                                                                                                         variables.add(variable);
                                                                                                                         cargarVariable(variable, tipo, "Nombre de variable", val_peek(1).sval, null, null, null, null, null, null);
                                                                                                                     }
                                                                                                                 } else {
                                                                                                                     yyerror("Error: la cantidad de tipos y variables del struct no coincide");
                                                                                                                 }
														tiposValidos.add(val_peek(6).sval);
																													 cargarVariable(val_peek(1).sval, "struct", "Nombre de struct", null, null, null, null, null, variables, null);
                                                                                                                 listaTipos.clear();
                                                                                                                 listaVariablesTemp.clear();
						    			}
break;
case 22:
//#line 112 "gramatica.y"
{yyerror("ERROR, Falta <> en la linea: " + lector.getNroLinea());}
break;
case 23:
//#line 113 "gramatica.y"
{yyerror("ERROR, Falta la palabra STRUCT en la linea: " + lector.getNroLinea());}
break;
case 24:
//#line 114 "gramatica.y"
{
                                            for(int i = 0; i< listaTipos.size(); i++){

                                            }
                                            yyerror("ERROR, Falta ID al final de la declaracion en la linea: " + lector.getNroLinea());
                                        }
break;
case 25:
//#line 122 "gramatica.y"
{
                                               yyval.sval = val_peek(0).sval;
                                             if (!esUnaFuncion || esNuevaFuncion) {

                                                        polacaFunciones.add(new ArrayList<>());
                                                        indiceFuncionActual = polacaFunciones.size() - 1;
                                                        pilaIndiceFuncion.push(indiceFuncionActual);
                                                        esNuevaFuncion = false;
                                                    }
                                             else{
                                             polacaFunciones.add(new ArrayList<>());
                                             indiceFuncionActual = polacaFunciones.size() - 1;
                                             pilaIndiceFuncion.push(indiceFuncionActual);
                                             }
                                                    esUnaFuncion = true;
                                            agregarTokenPolaca(val_peek(0).sval + "@" + ambitoActual);
                                            ambitoActual = ambitoActual + "@" + val_peek(0).sval;
					    /*polacaFunciones.add(new ArrayList<>());*/
                                            /*        indiceFuncionActual = polacaFunciones.size() - 1;*/
                                        }
break;
case 26:
//#line 145 "gramatica.y"
{

                                            String[] parametro = val_peek(4).sval.split(",");
                                            parametro[1] = parametro[1] + "@" + ambitoActual;


                                                        if (val_peek(8).sval.equals("LONGINT")) {
                                                            polacaFunciones.get(pilaIndiceFuncion.peek()).add("0");
                                                            lector.tablaSimbolos.addToken("0",272,"LONGINT");
                                                        } else if (val_peek(8).sval.equals("DOUBLE")) {
                                                            polacaFunciones.get(pilaIndiceFuncion.peek()).add("0.0");
                                                            lector.tablaSimbolos.addToken("0.0",275,"DOUBLE");
                                                        }
                                                        polacaFunciones.get(pilaIndiceFuncion.peek()).add("RET@" + ambitoActual);
                                            ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));
                                            /*polacaIndiceFuncion.get(pilaIndiceFuncion.peek()).add("RET@"+ ambitoActual  + "@" + $3.sval);*/
                                            String Ret_simbolo = "RET@" + ambitoActual + "@" + val_peek(6).sval;
                                            int token = this.RET;
                                            lector.tablaSimbolos.addToken(Ret_simbolo,token,val_peek(8).sval);
                                            cargarVariable(val_peek(6).sval, val_peek(8).sval, "Nombre de Funcion", null, null, null, parametro[0], parametro[1],null,null);
                                            /*lector.tablaSimbolos.borrarLexema($3.sval);*/
                                            int Indice= pilaIndiceFuncion.peek();
					    pilaIndiceFuncion.pop();
					    if (!pilaIndiceFuncion.isEmpty()) {
						indiceFuncionActual = pilaIndiceFuncion.peek();
					    }

					    esUnaFuncion = false;
					    esNuevaFuncion = true;
					    /*System.out.println("Antes de romperme" + pilaIndiceFuncion.peek());*/

					    if (funcionesConRetorno.contains(Indice)) {
					    funcionesConRetorno.remove(Indice);
					    }

                                            /*indiceFuncionActual = -1;*/
                                        }
break;
case 27:
//#line 183 "gramatica.y"
{   yyerror("ERROR, Falta la declaracion de la palabra reservada FUN en la linea: " + lector.getNroLinea());
                                            /*String[] parametro = $4.split(",");*/
                                            /*parametro[1] = parametro[1] + "@" + ambitoActual;*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                            /*cargarVariable($2.sval,$1.sval,"Nombre de Funcion", null, null, null, parametro[0], parametro[1]);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 28:
//#line 191 "gramatica.y"
{   yyerror("ERROR, Falta el ID de la funcion en la linea: " + lector.getNroLinea());

                                        }
break;
case 29:
//#line 195 "gramatica.y"
{   yyerror("ERROR, Falta de () a la hora de los parametros en la linea: " + lector.getNroLinea());
                                            /*cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null, null);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 30:
//#line 200 "gramatica.y"
{   yyerror("ERROR, Falta de parametros en la FUN en la linea: " + lector.getNroLinea());
                                            /*cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 31:
//#line 205 "gramatica.y"
{   yyerror("ERROR, Falta de BEGIN en la FUN en la linea: " + lector.getNroLinea());
                                            /*cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 32:
//#line 210 "gramatica.y"
{
                                            /*cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 33:
//#line 215 "gramatica.y"
{   yyerror("ERROR, Falsa cuerpo_funcion de funcion en la linea: " + lector.getNroLinea());
                                            /*cargarVariable($3.sval,$1.sval,"Nombre de Funcion", null, null, null);*/
                                            /*ambitoActual = ambitoActual.substring(0, ambitoActual.lastIndexOf("@"));*/
                                        }
break;
case 34:
//#line 222 "gramatica.y"
{esUnaFuncion = true;}
break;
case 35:
//#line 223 "gramatica.y"
{esUnaFuncion= true;}
break;
case 41:
//#line 232 "gramatica.y"
{
              				            if (esUnaFuncion) {

                                            agregarTokenPolaca(val_peek(1).sval);  /* Marca pendiente*/
                                            agregarTokenPolaca("#BI");
                                            /*int posicionPendiente = obtenerPosicionPolacaEnFuncion(indiceFuncionActual) - 2;*/
                                            /*saltosPendientes.putIfAbsent($2.sval, new ArrayList<>());*/
                                            /*saltosPendientes.get($2.sval).add(new int[] {indiceFuncionActual, posicionPendiente});*/
                                        } else {
                                            /* En el programa principal*/

                                            agregarTokenPolaca(val_peek(1).sval);
                                            agregarTokenPolaca("#BI");
                                            /*int posicionPendiente = obtenerPosicionPolaca() - 2;*/
                                            /*saltosPendientes.putIfAbsent($2.sval, new ArrayList<>());*/
                                            /*saltosPendientes.get($2.sval).add(new int[] {-1, posicionPendiente});  // -1 para indicar el cuerpo principal*/
                                        }
                                        }
break;
case 42:
//#line 250 "gramatica.y"
{yyerror("ERROR, Falta ';' al final de la sentencia en la linea: " + lector.getNroLinea());}
break;
case 43:
//#line 251 "gramatica.y"
{yyerror("ERROR, falta la ETIQUETA en la linea: " + lector.getNroLinea());}
break;
case 44:
//#line 254 "gramatica.y"
{
                                            DatosTablaSimbolos d = lector.tablaSimbolos.getDato(val_peek(0).sval);
                                            d.setUso("Nombre de etiqueta");
                                            lector.tablaSimbolos.setDato(d, val_peek(0).sval);
                                             if (esUnaFuncion) {
                                                 /* En el contexto de una función*/
                                                agregarTokenPolaca(val_peek(0).sval);
                                                 /*int posicionEtiqueta = obtenerPosicionPolacaEnFuncion(indiceFuncionActual);

                                                 if (saltosPendientes.containsKey($1.sval)) {
                                                     for (int[] posicionPendiente : saltosPendientes.get($1.sval)) {
                                                         int funcIndex = posicionPendiente[0];
                                                         int index = posicionPendiente[1];
                                                         if (funcIndex == indiceFuncionActual) {
                                                             polacaFunciones.get(funcIndex).set(index, String.valueOf(posicionEtiqueta));
                                                         }
                                                     }
                                                     saltosPendientes.remove($1.sval);
                                                 }*/
                                             } else {
                                                 /* En el programa principal*/
                                                 agregarTokenPolaca(val_peek(0).sval);
                                                 /*
                                                 int posicionEtiqueta = obtenerPosicionPolaca();

                                                 if (saltosPendientes.containsKey($1.sval)) {
                                                     for (int[] posicionPendiente : saltosPendientes.get($1.sval)) {
                                                         int funcIndex = posicionPendiente[0];
                                                         int index = posicionPendiente[1];
                                                         if (funcIndex == -1) {
                                                             polaca.set(index, String.valueOf(posicionEtiqueta));
                                                         }
                                                     }
                                                     saltosPendientes.remove($1.sval);
                                                 }*/
                                             }
                                        }
break;
case 46:
//#line 293 "gramatica.y"
{
                                         if (!funcionesConRetorno.contains(pilaIndiceFuncion.peek())) {
                                             /*agregarTokenPolaca("RET@" + ambitoActual);*/
                                             polacaFunciones.get(pilaIndiceFuncion.peek()).add("RET@" + ambitoActual);
                                             funcionesConRetorno.add(pilaIndiceFuncion.peek());
                                         }
                                        }
break;
case 47:
//#line 300 "gramatica.y"
{yyerror ("ERROR, falta parentesis en el ret");}
break;
case 48:
//#line 304 "gramatica.y"
{
                                                     if (esUnaFuncion) {
                                                         /* Resolver el salto condicional*/
                                                         int posicionCondicion = pilaFuncion.pop(); /* Posición del #BF*/
                                                         polacaFunciones.get(indiceFuncionActual).set(
                                                             posicionCondicion,
                                                             String.valueOf(polacaFunciones.get(indiceFuncionActual+2).size())
                                                         );

                                                         /* Obtener la posición de inicio del bloque*/
                                                         int posicionInicio = pilaFuncion.pop();

                                                         /* Salto incondicional al inicio del bucle*/
                                                         agregarTokenPolaca(String.valueOf(posicionInicio));
                                                         agregarTokenPolaca("#BI");

                                                         /* Etiqueta para el fin del bucle*/
                                                         agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                     } else {
                                                         /* Resolver el salto condicional*/
                                                         int posicionCondicion = pila.pop(); /* Posición del #BF*/
                                                         polaca.set(posicionCondicion, String.valueOf(polaca.size()+2));

                                                         /* Obtener la posición de inicio del bloque*/
                                                         int posicionInicio = pila.pop();

                                                         /* Salto incondicional al inicio del bucle*/
                                                         agregarTokenPolaca(String.valueOf(posicionInicio));
                                                         agregarTokenPolaca("#BI");

                                                         /* Etiqueta para el fin del bucle*/
                                                         agregarTokenPolaca(":L" + polaca.size());
                                                     }
                                                 }
break;
case 49:
//#line 339 "gramatica.y"
{yyerror("ERROR, falta palabra ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
break;
case 50:
//#line 340 "gramatica.y"
{yyerror("ERROR, falta la condicion del WHILE en la linea: " + lector.getNroLinea());}
break;
case 51:
//#line 341 "gramatica.y"
{yyerror("ERROR, falta parentesis '(' en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
break;
case 52:
//#line 342 "gramatica.y"
{yyerror("ERROR, falta parentesis ')' en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
break;
case 53:
//#line 343 "gramatica.y"
{yyerror("ERROR, falta parentesis en la declaracion de la condicion de WHILE en la linea: " + lector.getNroLinea());}
break;
case 54:
//#line 344 "gramatica.y"
{yyerror("ERROR, falta el cuerpo de la iteracion repeat en la linea: " + lector.getNroLinea());}
break;
case 55:
//#line 347 "gramatica.y"
{
					      if (esUnaFuncion) {
                                                      /* Guardar el inicio del bucle en la pila de funciones*/
                                                      int posicionInicio = polacaFunciones.get(indiceFuncionActual).size();
                                                      pilaFuncion.push(posicionInicio);

                                                      /* Etiqueta de inicio del bucle*/
                                                      agregarTokenPolaca(":L" + posicionInicio);
                                                  } else {
                                                      /* Guardar el inicio del bucle en la pila principal*/
                                                      int posicionInicio = polaca.size();
                                                      pila.push(posicionInicio);

                                                      /* Etiqueta de inicio del bucle*/
                                                      agregarTokenPolaca(":L" + posicionInicio);
                                                  }
                                        }
break;
case 56:
//#line 370 "gramatica.y"
{
                                              String cml = val_peek(2).sval;
                                              agregarTokenPolaca(cml);
                                              if (cml.startsWith("{") && cml.endsWith("}")) {
                                                DatosTablaSimbolos d = lector.tablaSimbolos.getDato(cml);
                                                lector.tablaSimbolos.borrarLexema(cml);
                                                cml = cml.substring(1, cml.length() - 1); /* Elimina los "{}"*/
                                                d.setUso("Cadena OUTF");
                                                lector.tablaSimbolos.addToken(cml,d);
                                              } else {
                                                DatosTablaSimbolos d = lector.tablaSimbolos.getDato(cml);
                                                if (d != null){
                                                  d.setUso("Cadena OUTF");
                                                  lector.tablaSimbolos.setDato(d, cml);
                                                }
                                              }

                                              agregarTokenPolaca("OUTF");
                                        }
break;
case 57:
//#line 391 "gramatica.y"
{
                                         agregarTokenPolaca("OUTF");
                                         }
break;
case 58:
//#line 394 "gramatica.y"
{yyerror("ERROR, Falta parámetro en sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 59:
//#line 395 "gramatica.y"
{yyerror("ERROR, Falta ';' en la sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 60:
//#line 396 "gramatica.y"
{yyerror("ERROR, Falta ';' en la sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 61:
//#line 397 "gramatica.y"
{yyerror("ERROR, Faltan los parentesis en la sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 62:
//#line 398 "gramatica.y"
{yyerror("ERROR, Faltan los parentesis en la sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 63:
//#line 399 "gramatica.y"
{yyerror("ERROR, tipo invalido como parametro para la sentencia OUTF en la linea: " + lector.getNroLinea());}
break;
case 64:
//#line 402 "gramatica.y"
{/*datos parametro*/
                                            yyval.sval= val_peek(1).sval + "," + val_peek(0).sval;
                                            cargarVariable(val_peek(0).sval,val_peek(1).sval,"Nombre de parametro",null,null,null,null,null, null,null);
				                        }
break;
case 65:
//#line 406 "gramatica.y"
{yyerror("ERROR, falta declaracion de TIPO o NOMBRE en el parametro de la linea: " + lector.getNroLinea());}
break;
case 66:
//#line 409 "gramatica.y"
{
                                                        /*verficar que la funcion que se invoca exista*/
                                                        if (lector.tablaSimbolos.obtenerToken(val_peek(3).sval) == -1)
                                                            yyerror("ERROR: la funcion a la que se quiere acceder no existe");
                                                        else if (!tieneAlcance(val_peek(3).sval))
                                                            yyerror("ERROR: la funcion a la que se quiere acceder en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");

                                                        agregarTokenPolaca(buscarNombreParametro(val_peek(3).sval));
                                                        agregarTokenPolaca(":=");

                                                        String nombreFunc = verAlcance(val_peek(3).sval);
                                                        agregarTokenPolaca(nombreFunc);

                                                        agregarTokenPolaca("CALL");
                                        }
break;
case 67:
//#line 425 "gramatica.y"
{yyerror("ERROR, falta parametro en la invocacion de la funcion en la linea: " + lector.getNroLinea());}
break;
case 71:
//#line 436 "gramatica.y"
{

                                                      int posicionBF = popPila();
                                                      if (esUnaFuncion) {
                                                          polacaFunciones.get(indiceFuncionActual).set(posicionBF, String.valueOf(polacaFunciones.get(indiceFuncionActual).size()-1));
                                                          /*agregarTokenPolaca(":L" +  polacaFunciones.get(indiceFuncionActual).size());*/
                                                      } else {
                                                          polaca.set(posicionBF, String.valueOf(polaca.size()-1));
                                                          /*agregarTokenPolaca(":L" + polaca.size());*/
                                                      }
                                        }
break;
case 72:
//#line 448 "gramatica.y"
{
                                                int posicionBF = popPila();
                                                if (esUnaFuncion) {
                                                polacaFunciones.get(indiceFuncionActual).set(posicionBF, String.valueOf(polacaFunciones.get(indiceFuncionActual).size()));
                                                 agregarTokenPolaca(":L" +  polacaFunciones.get(indiceFuncionActual).size());
                                                 }   else {
                                                    polaca.set(posicionBF, String.valueOf(polaca.size()));
                                                    agregarTokenPolaca(":L" + polaca.size());
                                                }
                                        }
break;
case 73:
//#line 458 "gramatica.y"
{yyerror("ERROR, Falta THEN luego de la condicion en la linea: " + lector.getNroLinea());}
break;
case 74:
//#line 459 "gramatica.y"
{yyerror("ERROR,falta de Condicion en la linea: " + lector.getNroLinea());}
break;
case 75:
//#line 460 "gramatica.y"
{yyerror("ERROR,falta el bloque ejecutable en la linea: " + lector.getNroLinea());}
break;
case 76:
//#line 461 "gramatica.y"
{yyerror("ERROR,falta END_IF; al final de la declaracion en la linea: " + lector.getNroLinea());}
break;
case 77:
//#line 462 "gramatica.y"
{yyerror("ERROR,falta ; al final de la declaracion del bloque IF en la linea: " + lector.getNroLinea());}
break;
case 78:
//#line 463 "gramatica.y"
{yyerror("ERROR,falta ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
break;
case 79:
//#line 464 "gramatica.y"
{yyerror("ERROR, falta ELSE luego de la sentencias de ejecucion en la linea: " + lector.getNroLinea());}
break;
case 80:
//#line 465 "gramatica.y"
{yyerror("ERROR,falta ';' al final de la declaracion en la linea: " + lector.getNroLinea());}
break;
case 81:
//#line 466 "gramatica.y"
{yyerror("ERROR, falta el bloque ejecutable en el ELSE en la linea: " + lector.getNroLinea());}
break;
case 82:
//#line 467 "gramatica.y"
{yyerror("ERROR, falta el bloque ejecutable en el IF en la linea: " + lector.getNroLinea());}
break;
case 83:
//#line 469 "gramatica.y"
{yyerror("ERROR,falta de parentesis en la linea: " + lector.getNroLinea());}
break;
case 84:
//#line 470 "gramatica.y"
{yyerror("ERROR,falta un parentesis ')' en la linea: " + lector.getNroLinea());}
break;
case 85:
//#line 471 "gramatica.y"
{yyerror("ERROR,falta un parentesis '(' en la linea: " + lector.getNroLinea());}
break;
case 86:
//#line 472 "gramatica.y"
{yyerror("ERROR,falta un parentesis ')' "); }
break;
case 87:
//#line 473 "gramatica.y"
{yyerror("ERROR,falta un parentesis '(' "); }
break;
case 88:
//#line 474 "gramatica.y"
{yyerror("ERROR,falta un parentesis '()' "); }
break;
case 89:
//#line 479 "gramatica.y"
{
                                            int posicion = popPila();
                                            if (esUnaFuncion) {
                                                        polacaFunciones.get(indiceFuncionActual).set(posicion, String.valueOf(polacaFunciones.get(indiceFuncionActual).size() + 2));  /* Actualiza el marcador en la Polaca de Funciones*/
                                                        agregarCheckpoint();
                                                        agregarTokenPolaca("");  /* Espacio reservado para BI.*/
                                                        agregarTokenPolaca("#BI");  /* Marcador del BI.*/
                                                        agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                    } else {
                                                        polaca.set(posicion, String.valueOf(polaca.size() + 2));  /* Actualiza el marcador en la Polaca Base*/
                                                        agregarCheckpoint();  /* Solo si es necesario.*/
                                                        agregarTokenPolaca("");  /* Espacio reservado para BI.*/
                                                        agregarTokenPolaca("#BI");  /* Marcador del BI.*/
                                                        agregarTokenPolaca(":L" + polaca.size());
                                                    }
                                                    /*imprimirPolaca();*/
                                        }
break;
case 90:
//#line 497 "gramatica.y"
{

                                                int posicion = popPila();

                                                if (esUnaFuncion) {
                                                    polacaFunciones.get(indiceFuncionActual).set(posicion, String.valueOf(polacaFunciones.get(indiceFuncionActual).size() + 2));/* Actualiza el marcador en la Polaca de Funciones*/
                                                    agregarCheckpoint();
                                                    agregarTokenPolaca("");  /* Espacio reservado para BI.*/
                                                    agregarTokenPolaca("#BI");  /* Marcador del BI.*/
                                                    agregarTokenPolaca(":L" + polacaFunciones.get(indiceFuncionActual).size());
                                                } else {
                                                    polaca.set(posicion, String.valueOf(polaca.size() + 2));  /* Actualiza el marcador en la Polaca Base*/
                                                    agregarCheckpoint();
                                                    agregarTokenPolaca("");  /* Espacio reservado para BI.*/
                                                    agregarTokenPolaca("#BI");  /* Marcador del BI.*/
                                                    agregarTokenPolaca(":L" + polaca.size());
                                                }
                                                /*imprimirPolaca();*/
                                            }
break;
case 91:
//#line 519 "gramatica.y"
{}
break;
case 93:
//#line 528 "gramatica.y"
{
                                            /*agregarTokenPolaca($1.sval);*/
                                            /*agregarTokenPolaca($3.sval);*/
                                            agregarTokenPolaca(val_peek(1).sval);
                                            agregarCheckpoint();  /* Guardamos la posición del BF en la pila para backpatching.*/
                                            agregarTokenPolaca("");  /* Espacio reservado para BF.*/
                                            agregarTokenPolaca("#BF");  /* Branch False si la condición es falsa.*/
                                        }
break;
case 94:
//#line 536 "gramatica.y"
{yyerror("ERROR, falta comparador en comparacion en la linea: " + lector.getNroLinea());}
break;
case 95:
//#line 540 "gramatica.y"
{yyval = val_peek(1);
                                           int j = 0;
                                            Integer cantidadComas = 0;
                                            for (String elemento : expresionTemp) {
                                                       if (elemento.equals(",")) {
                                                           cantidadComas++;
                                                       }
                                                       System.out.println("expresion temp:" +elemento);
                                                   }
                                           System.out.println("size: " + listaVariablesTemp.size());
                                           if (listaVariablesTemp.size() > cantidadComas + 1){
                                            yyerror("ERROR: en la asignacion multiple hay mas Variables que Expresiones ");
                                           }
                                           else {
                                            if(listaVariablesTemp.size() < cantidadComas + 1){
                                              yyerror("ERROR: en la asignacion multiple hay mas Expresiones que Variables");
                                            }
                                           }
                                           for(int i = 0 ; i< listaVariablesTemp.size(); i++){

                                              if(!tieneAlcance(listaVariablesTemp.get(i)))
                                                                   yyerror("ERROR: la variable " + listaVariablesTemp.get(i) + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                              while(j < expresionTemp.size() && expresionTemp.get(j) != ","){


                                               if(expresionTemp.get(j).matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                                                                                       if((lector.tablaSimbolos.getDato(expresionTemp.get(j) + "@" + ambitoActual).getUso() != null)
                                                                                       && (!lector.tablaSimbolos.getDato(expresionTemp.get(j) + "@" + ambitoActual).getUso().equals("Nombre de Funcion")))
                                                                                          agregarTokenPolaca(expresionTemp.get(j) + "@" + ambitoActual);
                                                                                       lector.tablaSimbolos.borrarLexema(expresionTemp.get(j));
                                                                   }
                                               else
                                                    agregarTokenPolaca(expresionTemp.get(j));
                                               j++;
                                              }
                                              j++;
                                                                String var = verAlcance(listaVariablesTemp.get(i));
                                                                if (var != null)
                                                                    agregarTokenPolaca(var);
                                                                agregarTokenPolaca(val_peek(2).sval);
                                                                lector.tablaSimbolos.borrarLexema(listaVariablesTemp.get(i));
                                                             }
                                                           esAsignacionMultiple = false;
                                                           listaVariablesTemp.clear();
                                                           expresionTemp.clear();

                                        }
break;
case 96:
//#line 590 "gramatica.y"
{
                                            yyval.ival = val_peek(2).ival + val_peek(0).ival;
                                            if(esAsignacionMultiple)
                                                expresionTemp.add("+");
                                            else
                                                agregarTokenPolaca("+");
                                        }
break;
case 97:
//#line 598 "gramatica.y"
{yyval.ival = val_peek(2).ival - val_peek(0).ival;
                                                                        if(esAsignacionMultiple)
                                                                        	expresionTemp.add("-");
                                    				    else
                                                                        	agregarTokenPolaca("-");
                                        }
break;
case 98:
//#line 604 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());
						    								 agregarTokenPolaca("+");
						    			}
break;
case 99:
//#line 607 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());
						    								 agregarTokenPolaca("-");
						    			}
break;
case 100:
//#line 615 "gramatica.y"
{

                                            agregarTokenPolaca("TOD");
                                        }
break;
case 101:
//#line 619 "gramatica.y"
{yyerror("ERROR, falta de expresion en la linea: " + lector.getNroLinea());}
break;
case 102:
//#line 620 "gramatica.y"
{yyval.sval = val_peek(0).sval;
                                        }
break;
case 103:
//#line 625 "gramatica.y"
{

                                        if(esAsignacionMultiple){
                                            expresionTemp.add(val_peek(0).sval);
                                            expresionTemp.add("*");}
                                        else{
                                            if(val_peek(0).sval.matches("^-?\\d+(\\.\\d+)?$") || val_peek(0).sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$"))
                                               agregarTokenPolaca(val_peek(0).sval);
                                                else {
                                                      String var = verAlcance(val_peek(0).sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            agregarTokenPolaca("*");
                                            }

                                        }
break;
case 104:
//#line 646 "gramatica.y"
{

                                            if(esAsignacionMultiple){
                                                expresionTemp.add(val_peek(0).sval);
                                                expresionTemp.add("/");}
                                            else{
                                                if(val_peek(0).sval.matches("^-?\\d+(\\.\\d+)?$") || val_peek(0).sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$"))
                                                   agregarTokenPolaca(val_peek(0).sval);
                                                else {
                                                      String var = verAlcance(val_peek(0).sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            agregarTokenPolaca("/");
                                            }

                                        }
break;
case 105:
//#line 667 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
break;
case 106:
//#line 668 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
break;
case 107:
//#line 669 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
break;
case 108:
//#line 670 "gramatica.y"
{yyerror("ERROR, hay 2 operadores en la linea: " + lector.getNroLinea());}
break;
case 109:
//#line 673 "gramatica.y"
{yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
break;
case 110:
//#line 674 "gramatica.y"
{yyerror("ERROR, falta de operando en la linea: " + lector.getNroLinea());}
break;
case 111:
//#line 676 "gramatica.y"
{

                                        agregarTokenPolaca("TOD");
                                        }
break;
case 112:
//#line 681 "gramatica.y"
{yyval.sval = val_peek(0).sval;
                                            if(esAsignacionMultiple){
                                                String var = verAlcance(val_peek(0).sval);
                                                DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(var);
                                                if((dato != null) && (dato.getTipo().equals("struct"))){
                                                    ArrayList<String> variables = dato.getListaVar();
                                                    for(int i=0; i < variables.size(); i++){
                                                        expresionTemp.add(variables.get(i));
                                                        if(i != variables.size() - 1)
                                                            expresionTemp.add(",");
                                                    }
                                                }
                                            else
                                                expresionTemp.add(val_peek(0).sval);
                                            }
                                            else
                                            {
                                                if(val_peek(0).sval.matches("^-?\\d+(\\.\\d+)?$") || val_peek(0).sval.matches("^-?[0-9]+\\.[0-9]+(d[-+]?[0-9]+)?$") || val_peek(0).sval.matches("^-?0[xX][0-9a-fA-F]+$"))
                                                    agregarTokenPolaca(val_peek(0).sval);
                                                else {
                                                      String var = verAlcance(val_peek(0).sval);
                                                      if (var != null) {
                                                          if ((lector.tablaSimbolos.getDato(var).getUso() != null)
                                                          && (!lector.tablaSimbolos.getDato(var).getUso().equals("Nombre de Funcion")))
                                                                agregarTokenPolaca(var);
                                                      }
                                                }
                                            }
                                        }
break;
case 114:
//#line 717 "gramatica.y"
{yyval.sval= val_peek(0).sval;
                                              if(!tieneAlcance(val_peek(0).sval))
                                                yyerror("ERROR: la variable " + val_peek(0).sval + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                              lector.tablaSimbolos.borrarLexema(val_peek(0).sval);
                                              /*agregarTokenPolaca($1.sval);*/
                                        }
break;
case 115:
//#line 724 "gramatica.y"
{   yyval.sval= val_peek(0).sval;
                                            if(!tieneAlcance(val_peek(0).sval))
                                              yyerror("ERROR: la variable " + val_peek(0).sval + " en la linea " + lector.getNroLinea() + " no se encuentra en el alcance permitido");
                                            lector.tablaSimbolos.borrarLexema(val_peek(0).sval);
                                            /*agregarTokenPolaca($1.sval);*/
                                        }
break;
case 116:
//#line 730 "gramatica.y"
{
                                                    yyval.sval = val_peek(0).sval;
                                                    Long valor = Long.parseLong(val_peek(0).sval);
                                                    if (valor == 2147483648L){
                                                        yyerror("ERROR, El número está fuera del rango permitido para un longint positivo en la linea: " + lector.getNroLinea());
                                                    }
                                                    else{
							    int token =   this.LONGINT;
							    lector.tablaSimbolos.addToken(val_peek(0).sval,token,"LONGINT");
					            }
                                                    /*agregarTokenPolaca($1.sval);*/
                                        }
break;
case 117:
//#line 742 "gramatica.y"
{
                                                    yyval.sval = "-" + val_peek(0).sval; /*TODO: posible error*/
                                                    String lexema = '-'+ val_peek(0).sval;
                                                    Long valor = Long.parseLong(val_peek(0).sval);
                                                    if (valor > 2147483648L){

                                                    }
                                                    else {
                                                        	int token =   this.LONGINT;
                                                                lector.tablaSimbolos.addToken(lexema,token,"LONGINT");
                                                    }
                                                    /*agregarTokenPolaca(lexema);*/
                                        }
break;
case 118:
//#line 756 "gramatica.y"
{
                                                    yyval.sval = val_peek(0).sval;
                                                    String hexa = val_peek(0).sval;
                                                    if (hexa.startsWith("0x") || hexa.startsWith("0X")) {
                                                        hexa = hexa.substring(2);
                                                    }
                                                    long num = Long.parseLong(hexa, 16);
                                                    long maxValorAbsoluto = 2147483648L;
                                                    if (num == maxValorAbsoluto){
                                                        yyerror("ERROR, El número está fuera del rango permitido para un HEXA positivo en la linea: " + lector.getNroLinea());
                                                    }
                                                    int token =   this.LONGINT;
                                                    lector.tablaSimbolos.addToken(val_peek(0).sval,token,"LONGINT");
                                                    /*agregarTokenPolaca($1.sval);*/
                                        }
break;
case 119:
//#line 771 "gramatica.y"
{
                                                    yyval.sval = "-" + val_peek(0).sval;
                                                    String lexema = '-'+ val_peek(0).sval;
                                                    int token =   this.LONGINT;
                                                    lector.tablaSimbolos.addToken(lexema,token,"LONGINT");
                                                    /*agregarTokenPolaca(lexema);*/
                                        }
break;
case 120:
//#line 778 "gramatica.y"
{
                                                    yyval.sval = val_peek(0).sval ; /* valor del número*/
                                                    String valor = val_peek(0).sval;
                                                    try {
                                                        String numeroStr = valor;

                                                        if (numeroStr.contains("d") || numeroStr.contains("D")) {
                                                          numeroStr = numeroStr.replace('d', 'E').replace('D', 'E');
                                                        }

                                                        /* Convertimos el valor a BigDecimal*/
                                                        BigDecimal numero = new BigDecimal(numeroStr);
                                                        BigDecimal min = new BigDecimal("2.2250738585072014E-308");
                                                        BigDecimal max = new BigDecimal("1.7976931348623157E+308");

                                                        /* Comparamos el número con los límites permitidos*/
                                                        if ((numero.compareTo(max) > 0) || (numero.compareTo(min) < 0 && numero.compareTo(BigDecimal.ZERO) != 0)){
                                                          yyerror("ERROR, El número está fuera del rango permitido para un double positivo en la linea: " + lector.getNroLinea());
                                                        } else {
                                                          int token = DOUBLE;
                                                          lector.tablaSimbolos.addToken(valor, token, "DOUBLE");  /* Añade el token*/
                                                          /*agregarTokenPolaca($1.sval);*/
                                                        }

                                                      } catch (NumberFormatException e) {
                                                        yyerror("Formato de número inválido.");
                                                      }

                                        }
break;
case 121:
//#line 807 "gramatica.y"
{

                                                    yyval.sval = "-" + val_peek(0).sval;
                                                    String valor = val_peek(0).sval;
                                                    try {
                                                          String numeroStr = valor;
                                                          if (numeroStr.contains("d") || numeroStr.contains("D")) {
                                                            numeroStr = numeroStr.replace('d', 'E').replace('D', 'E');
                                                          }
                                                            BigDecimal numero = new BigDecimal(numeroStr).negate();
                                                            BigDecimal min = new BigDecimal("-1.7976931348623157E+308");
                                                            BigDecimal max = new BigDecimal("-2.2250738585072014E-308");
                                                            if (numero.compareTo(max) > 0 || numero.compareTo(min) < 0) {
                                                              yyerror("ERROR, El número está fuera del rango permitido para un double negativo en la linea: " + lector.getNroLinea());
                                                            } else {
                                                              int token = this.DOUBLE;
                                                              String negativo = "-" + valor;
                                                              /*agregarTokenPolaca("negativo");*/
                                                              lector.tablaSimbolos.addToken(negativo, token,"DOUBLE");
                                                            }
                                                          } catch (NumberFormatException e) {
                                                            yyerror("Formato de número inválido.");
                                                          }
                                        }
break;
case 122:
//#line 833 "gramatica.y"
{
                                            esAsignacionMultiple = true;
                                            listaVariablesTemp.add(val_peek(0).sval);
                                        }
break;
case 123:
//#line 837 "gramatica.y"
{
                                            esAsignacionMultiple = true;
                                            listaVariablesTemp.add(val_peek(0).sval);
                                            }
break;
case 125:
//#line 842 "gramatica.y"
{
                                            String var = verAlcance(val_peek(0).sval);
                                            DatosTablaSimbolos dato = lector.tablaSimbolos.getDato(var);
                                            if((dato != null) && (dato.getTipo().equals("struct"))){
                                                esAsignacionMultiple = true;
                                                ArrayList<String> variables = dato.getListaVar();
                                                for(String varAux: variables){
                                                    listaVariablesTemp.add(varAux);
                                                }
                                            }
                                            else
                                                listaVariablesTemp.add(val_peek(0).sval);
                                        }
break;
case 126:
//#line 858 "gramatica.y"
{listaVariablesTemp.add(val_peek(2).sval + "_" + val_peek(0).sval);}
break;
case 127:
//#line 861 "gramatica.y"
{yyval.sval = val_peek(2).sval + "_" + val_peek(0).sval; }
break;
case 128:
//#line 864 "gramatica.y"
{expresionTemp.add(",");}
break;
case 129:
//#line 867 "gramatica.y"
{/*expresionTemp.add($3.sval);*/
                                         /*expresionTemp.add(",");*/
                                        }
break;
case 130:
//#line 872 "gramatica.y"
{/*expresionTemp.add($1.sval);*/
                                        }
break;
case 131:
//#line 878 "gramatica.y"
{   listaTipos.add(val_peek(0).sval);

                                        }
break;
case 132:
//#line 881 "gramatica.y"
{yyerror("ERROR, falta de ',' en la lista en la linea: " + lector.getNroLinea());}
break;
case 133:
//#line 882 "gramatica.y"
{listaTipos.add(val_peek(0).sval);}
break;
case 136:
//#line 893 "gramatica.y"
{tipoId = true;
                                            lector.tablaSimbolos.borrarLexema(val_peek(0).sval);
                                            String nombre = verAlcance(val_peek(0).sval);
                                            DatosTablaSimbolos dato= lector.tablaSimbolos.getDato(nombre);
                                            if(dato == null || (!dato.getUso().equals("Nombre de tipo") && !dato.getUso().equals("Nombre de struct")))
                                                yyerror("ERROR: Tipo no válido: " + val_peek(0).sval + " en la línea: " + lector.getNroLinea());
                                        }
break;
case 137:
//#line 900 "gramatica.y"
{/*$$ = $1*/
                                        lector.tablaSimbolos.borrarLexema(val_peek(0).sval);
                                        }
break;
//#line 2167 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run(AnalizadorLexico lex)
{
  lector = lex;
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
