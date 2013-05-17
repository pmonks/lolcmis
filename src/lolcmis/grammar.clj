;
; Copyright © 2013 Peter Monks (pmonks@gmail.com)
;
; This work is licensed under the Creative Commons Attribution-ShareAlike 3.0
; Unported License. To view a copy of this license, visit
; http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to Creative
; Commons, 444 Castro Street, Suite 900, Mountain View, California, 94041, USA.
;

; LOLCMIS (LOLCODE + CMIS) grammar

(ns lolcmis.grammar)

; LOLCMIS grammar, partially based on the LOLCODE grammars at http://forum.lolcode.com/viewtopic.php?id=318
(def lolcmis-grammar "
  (* Program structure *)
  Program               = <SkipLine*> Header StatementList <'KTHXBYE'> <SkipLine*>
  Header                = <'HAI'> <NewLine> |
                          <'HAI'> <Whitespace> FloatLiteral <NewLine>
  StatementList         = Statement*

  (* Statements *)
  Statement             = <SkipLine> |
                          ImportStatement |
                          OutputStatement |
                          InputStatement |
                          VariableDeclaration |
                          Assignment
  SkipLine              = Comment |
                          NewLine
  Comment               = SingleLineComment | MultiLineComment
  SingleLineComment     = 'BTW' (Whitespace AnyText)? NewLine
  MultiLineComment      = 'OBTW' ((Whitespace | NewLine) AnyText?)+ 'TLDR'
  ImportStatement       = <'CAN HAZ'> <Whitespace> Identifier <'?'> <NewLine>
  OutputStatement       = <'VISIBLE'> <Whitespace> (Identifier | Literal) <NewLine>
  InputStatement        = <'GIMMEH'> <Whitespace> Identifier <NewLine>
  VariableDeclaration   = <'I HAS A'> <Whitespace> Identifier (<Whitespace> (<'ITZ'> <Whitespace> Expression | <'ITZ A'> <Whitespace> Type))? <NewLine>
  Assignment            = Identifier <Whitespace> <'R'> <Whitespace> Expression <NewLine>

  (* Non-statements *)
  Expression            = Identifier |
                          Literal |
                          CastExpression
  CastExpression        = Identifier <Whitespace> <'IS NOW A'> <Whitespace> Type
  Literal               = StringLiteral |
                          IntegerLiteral |
                          FloatLiteral |
                          BooleanLiteral |
                          VoidLiteral
  StringLiteral         = <DoubleQuote> (EscapedDoubleQuote | StringCharacter)* <DoubleQuote>
  BooleanLiteral        = TrueLiteral | FalseLiteral

  (* Almost-terminals *)
  NewLine               = OptionalWhitespace ('\\n' | '\\r' | '\\r\\n' | '\\u0085' | '\\u2028' | '\\u2029' | '\\u000B')
  Identifier            = !ReservedWord #'[_\\p{Alpha}]\\w*'

  (* Terminals *)
  AnyText               = #'.*'
  Whitespace            = #'[ \\t]+'
  OptionalWhitespace    = #'[ \\t]*'
  IntegerLiteral        = #'\\d+'
  FloatLiteral          = #'\\d+\\.\\d+'
  TrueLiteral           = 'WIN'
  FalseLiteral          = 'LOSE'
  VoidLiteral           = 'NOOB'
  Type                  = 'YARN' | 'NUMBR' | 'NUMBAR' | 'TROOF' | 'NOOB'
  DoubleQuote           = '\"'
  EscapedDoubleQuote    = '\\\\\"'
  StringCharacter       = #'[^\"]'

  (* Reserved words *)
  ReservedWord          = BooleanLiteral | VoidLiteral | Type
  ")







; Reference grammar copied and modified from http://forum.lolcode.com/viewtopic.php?id=318

(comment
; Grammar definition and parser function
(def ^:private lolcmis-grammar "
  Program               = Header BodyStatementList 'KTHXBAI'
  Header                = 'HAI' T_STMT_END |
                          'HAI' T_IDENTIFIER T_STMT_END
  BodyStatementList     = BodyStatement |
                          BodyStatement BodyStatementList
  BodyStatement         = Statement (* |
                          FunctionDefinition T_STMT_END *)
  Statement             = LoopDefinition T_STMT_END |
                          ConditionalStatement T_STMT_END |
                          SwitchStatement T_STMT_END |
                          Declaration T_STMT_END |
                          Assignment T_STMT_END |
                          Expression T_STMT_END |
                          InputStatement T_STMT_END
(*
  FunctionDefinition    = 'HOW DUZ I' T_IDENTIFIER FunctionArgs T_STMT_END FunctionStatementList 'IF U SAY SO'
  FunctionArgs          = 'YR' T_IDENTIFIER |
                          FunctionArgs 'AN' 'YR' T_IDENTIFIER
  FunctionStatementList = FunctionStatement |
                          FunctionStatement FunctionStatementList
  FunctionStatement     = Statement |
                          'GTFO' T_STMT_END |
                          'FOUND YR' Expression T_STMT_END
*)
  LoopDefinition        = 'IM IN YR' T_IDENTIFIER T_IDENTIFIER 'YR' T_IDENTIFIER LoopCondition T_STMT_END LoopStatementList 'IM OUTTA YR' T_IDENTIFIER
  LoopCondition         = 'TIL' Expression |
                          'WILE' Expression
  LoopStatementList     = LoopStatement |
                          LoopStatement LoopStatementList
  LoopStatement         = Statement |
                          'GTFO' T_STMT_END
  ConditionalStatement  = 'O RLY?' T_STMT_END 'YA RLY' StatementList ConditionalElseList 'OIC' T_STMT_END
  ConditionalElseList   = 'MEBBE' Expression T_STMT_END StatementList ConditionalElseList |
                          'NO WAI' StatementList
  SwitchStatement       = 'WTF?'' T_STMT_END CaseList
  CaseList              = 'OMG' Literal T_STMT_END StatementList CaseList |
                          'OMGWTF' T_STMT_END StatementList 'OIC' |
                          'OIC'
  Declaration           = 'I HAS A' T_IDENTIFIER |
                          'I HAS A' T_IDENTIFIER 'ITZ' Expression |
                          'I HAS A' T_IDENTIFIER 'ITZ' 'A' Type
  Assignment            = T_IDENTIFIER 'R' Expression
  InputStatement        = 'GIMMEH' T_IDENTIFIER T_STMT_END
  Expression            = (* FunctionCall | *)
                          CastExpression |
                          T_IDENTIFIER |
                          Literal
(*
  FunctionCall          = T_IDENTIFIER ArgumentsWithStart |
                          'BOTH SAEM' ArgumentsWithStart |
                          T_CAST T_IDENTIFIER TypeWithAs
*)
  TypeWithAs            = Type |
                          'A' Type
(*
  ArgumentsWithStart    = Arguments |
                          'OF' Arguments
  Arguments             = Expression |
                          Expression Expression |
                          Expression 'AN' Expression |
                          ExtendedArgs
  ExtendedArgs          = Expression 'MKAY' |
                          Expression ExtendedArgs |
                          Expression 'AN' ExtendedArgs
*)
  CastExpression        = T_IDENTIFIER 'IS NOW A' Type
  Type                  = 'YARN' |
                          'NUMBR' |
                          'NUMBAR' |
                          'TROOF' |
                          'NOOB'
  Literal               = T_STRING_LITERAL |
                          T_INT_LITERAL |
                          T_FLOAT_LITERAL |
                          'WIN' |
                          'LOSE' |
                          'NOOB'

  T_STMT_END            = '\\n' | '\\r' | '\\r\\n'
  T_IDENTIFIER          = #'\\S+'
  T_STRING_LITERAL      = #'\".*\"'
  T_INT_LITERAL         = #'\\d+'
  T_FLOAT_LITERAL       = #'\\d+\\.\\d+'
  ")
)
