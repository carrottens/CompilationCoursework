import java.io.IOException;
import java.lang.management.MemoryUsage;

/**
 *
 * SCC312
 * Coursework 2
 * Compilation
 * 
 * Syntax Analyser extends AbstractSyntaxAnalyser
 * It recognises its input (tokens from Lexical Analyser) as 
 * valid or invalid sentences in thelanguage specified by the grammar.
 * (Rules of the grammar applied are given above 
 * every method that is recognising a non-terminal.)
 * 
 * SA achieves this by implementing of a recursive descent parser.
 * Outcomes:
 * Makes sure that a user’s source program is syntactically correct.
 * Generate appropriate and helpful error messages using Generate class.
 * Terminates on encountering and reporting the first error. 
 * 
 * @author: Viltene
 *
 **/

public  class SyntaxAnalyser extends AbstractSyntaxAnalyser 
{
    public SyntaxAnalyser(String fileName)
	{
        /** Tries to initialise the LA from the abstract SA class to process input */
        try
        {
            lex = new LexicalAnalyser(fileName);
        }
        catch (IOException ex)
        {
            System.out.println("Failed initialisation of Lexical Analyser");
        }
	} // end of constructor method

    /** 
     * 
     * This method overrides the method in AbstractSyntaxAnalyser.
     * It recognises the distinguished symbol (<statement part>)
     * by applying the grammar rule:
     * <statment part> ::= begin <statement list> end
     * Throws a Compilation Exception if the root is not recognised
     * 
     **/
	@ Override
    public void _statementPart_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<statement part>");

        /* try to parse "begin" and report accordingly */
        try 
        {
            acceptTerminal(Token.beginSymbol); 
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <statement part>. Was expecting \"begin\" to start the <statement part>", cexc);
        }

        /* try to parse <statement list> and report accordingly */
        try
        {
            _statementList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <statement part> while parsing <statement list>", cexc);
        }
        
        /* try to recognise "end" and report accordingly */
        try
        {
            acceptTerminal(Token.endSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <statement part>. Was expecting \"end\" to close the <statement part>", cexc);
        }

        myGenerate.finishNonterminal("<statement part>");
    } 

    /** 
     * 
     * This method overrides the method in AbstractSyntaxAnalyser.
     * It accepts the terminal int symbol if it is 
     * satisfying the rule of the grammar
     * Reports an error if the terminal is not recognised
     * 
     **/
	@Override 
    public void acceptTerminal(int symbol) throws IOException, CompilationException
    {
        if (nextToken.symbol == symbol) // if the terminal is recognise
        {
            myGenerate.insertTerminal(nextToken); // accept it
            nextToken = lex.getNextToken(); // move on to another
        }
        else // if the terminal is not recognised
        {
            myGenerate.reportError(nextToken, ". Was expecting " + Token.getName(symbol)); // report an error
        }
    }

    /** 
     * 
     * This method recognises the non-terminal (<statement list>)
     * by applying the grammar rule:
     * <statment list> ::= <statement>{< ; <statement list>}
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _statementList_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<statement list>");
        
        /* try to parse <statement> and report accordingly */
        try
        {
            _statement_(); // try to parse <statement>
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <statement list> while parsing <statement>", cexc);
        }

        /* try to parse {< ; <statement list>} and report accordingly */
        while (nextToken.symbol == Token.semicolonSymbol)
        {
            try
            {
                acceptTerminal(Token.semicolonSymbol); // try to parse ;
                _statementList_(); // try to parse <statement list>
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement list> while parsing \"; <statement>\"", cexc);
            }
        }
            myGenerate.finishNonterminal("<statement list>");
    }
        
    /** 
     * 
     * This method recognises the non-terminal (<statement>)
     * by applying the grammar rule:
     * <statment> ::= <assignement statement> | <if statement> | < while statement> |
     *                <procedure statement> | <until statement> | <for statement>
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _statement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<statement>");

        /**
         * Try to parse <statement> using its First set
         * 
         */
        switch(nextToken.symbol)
        {
            /* try to parse <assignement statement> and report accordingly */
            case Token.identifier:
            try
            {
                _assignementStatement_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <assignement statement>", cexc);
            }

            /* try to parse <if statement> and report accordingly */
            case Token.ifSymbol:
            try
            {
                _ifStatement_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <if statement>", cexc);
            }

            /* try to parse <while statement> and report accordingly */
            case Token.whileSymbol: 
            try
            {
                _whileStatement_(); 
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <while statement>", cexc);
            }
            
            /* try to parse <procedure statement> and report accordingly */
            case Token.callSymbol: 
            try
            {
                _procedureStatement_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <procedure statement>", cexc);
            }

            /* try to parse <until statement> and report accordingly */
            case Token.doSymbol: 
            try
            {
                _untilStatement_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <until statement>", cexc);
            }

            /* try to parse <for statement> and report accordingly */
            case Token.forSymbol:
            try
            {
                _forStatement_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <statement> while parsing <for statement>", cexc);
            }

            default: // if neither report error
            myGenerate.reportError(nextToken, " in <statement>. Was expecting \"identifier\", \"if\", \"while\", \"call\", \"do\" or \"for\" to start a new <statement>"); 
        }
        
        myGenerate.finishNonterminal("<statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<assignement statement>)
     * by applying the grammar rule:
     * <assignement statement> ::= identifier := <new ASSIGNEMENT STATEMENT REMAINDER>
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _assignementStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<assignement statement>");

        /* try to recognise an "identifier" and report accordingly */
        try
        {
            acceptTerminal(Token.identifier);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <assignement statement>. Was expecting <assignement statement> to be started by an \"identifier\"", cexc);
        }
        
        /* try to recognise a "::=" and report accordingly */
        try
        {
            acceptTerminal(Token.becomesSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <assignement statement>. Was expecting \"::=\" after an \"identifier\"", cexc);
        }

        /* try to parse a <new ASSIGNEMENT STATEMENT REMAINDER> and report accordingly */
        try
        {
            _newAssignementStatementRemainder_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <assignement statement> while parsing <new ASSIGNEMENT STATEMENT REMAINDER>", cexc);
        }

        myGenerate.finishNonterminal("<assignement statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<new ASSIGNEMENT STATEMENT REMAINDER>)
     * by applying the grammar rule:
     * <new ASSIGNEMENT STATEMENT REMAINDER> ::= <expression> | stringConstant
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _newAssignementStatementRemainder_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<new ASSIGNEMENT STATEMENT REMAINDER>");

        /** Try to parse <new ASSIGNEMENT STATEMENT REMAINDER>  using its First set */
        switch(nextToken.symbol)
        {
            /* try to recognise a "stringConstant" and report accordingly */
            case Token.stringConstant:
            acceptTerminal(Token.stringConstant);
            break;

            /** First(<expression>) used to try to parse an <expression> */
            case Token.identifier:
            try
            {
                _expression_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new ASSIGNEMENT STATEMENT REMAINDER> while parsing <expression> begining by \"identifier\"", cexc);
            }
                
            case Token.numberConstant:
            try
            {
                _expression_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new ASSIGNEMENT STATEMENT REMAINDER> while parsing <expression> begining by \"numberConstant\"", cexc);
            }

            case Token.leftParenthesis:
            try
            {
                _expression_();
                break;
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new ASSIGNEMENT STATEMENT REMAINDER> while parsing <expression> begining by \"(\"", cexc);
            }

            /** If neither... Report an error */
            default:
            myGenerate.reportError(nextToken, " in <new ASSIGNEMENT STATEMENT REMAINDER>. Was expecting \"(\", \"numberConstant\", \"identifier\" or \"stringConstant\"");
        }

        myGenerate.finishNonterminal("<new ASSIGNEMENT STATEMENT REMAINDER>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<if statement>)
     * by applying the grammar rule:
     * <if statement> ::= if <condition> then <statement list> [else <statement list>] end if
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _ifStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<if statement>");
        
        /** Try to recognise "if" */
        try
        {    
            acceptTerminal(Token.ifSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement>. Was expecting an \"if\" in the beginning of an <if statement>", cexc);
        }

        /** Try to parse <condition> */
        try
        {
            _condition_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement> while parsing a <condition>", cexc);
        }

        /** Try to recognise "then" */
        try
        {
            acceptTerminal(Token.thenSymbol);
        }
        catch(CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement>. Was expecting \"then\" to follow a <condition>", cexc);
        }
        
        /** Try to parse a <statement list> */
        try
        {
            _statementList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement> while parsing a <statement list> ", cexc);
        }

        if(nextToken.symbol==Token.elseSymbol)
        {
            /** try to parse  [else <statement list>] */
            try
            {
                acceptTerminal(Token.elseSymbol);
                _statementList_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <if statement> while parsing an [else <statement list>] ", cexc);
            }
        }
        
        /** Try to recognise an "end" */
        try
        {
            acceptTerminal(Token.endSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement>. Was expecting \"end\"", cexc);
        }
        /** Try to recognise an "if" */
        try
        {
            acceptTerminal(Token.ifSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <if statement>. Was expecting \"if\" after \"end\" to close the <if statement>", cexc);
        }
        
        myGenerate.finishNonterminal("<if statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<while statement>)
     * by applying the grammar rule:
     * <while statement> ::= while <condition> loop <statement list> end loop
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _whileStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<while statement>");
        
        /** Try to recognise "while" */
        try
        {
            acceptTerminal(Token.whileSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement>. Was expecting a \"while\" in the beginning of a <while statement>", cexc);
        }
        
        /** Try to parse a <condition> */
        try
        {
            _condition_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement> while parsing a <condition> ", cexc);
        }

        /** Try to recognise a "loop" */
        try
        { 
            acceptTerminal(Token.loopSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement>. Was expecting \"loop\" after <condition>", cexc);
        }

        /** Try to parse a <statement list> */
        try
        {
            _statementList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement> while parsing <statement list>", cexc);
        }

        /** Try to recognise a "end" */
        try
        {
            acceptTerminal(Token.endSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement>. Was expecting \"end\" after a <statement list>", cexc);
        }

        /** Try to recognise a "loop" */
        try
        {
            acceptTerminal(Token.loopSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <while statement>. Was expecting \"loop\" after \"end\" to close off the <while statement>", cexc);
        }
        
        myGenerate.finishNonterminal("<while statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<procedure statement>)
     * by applying the grammar rule:
     * <procedure statement> ::= call identifier ( <argument list> )
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _procedureStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<procedure statement>");

        /** Try to recognise "call" */
        try
        {
            acceptTerminal(Token.callSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <procedure statement>. Was expecting a \"call\" in the beginning of a <procedure statement>", cexc);
        }

        /** Try to recognise "identifier" */
        try
        {
            acceptTerminal(Token.identifier);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <procedure statement>. Was expecting a \"identifier\" after \"call\"", cexc);
        }

        /** Try to recognise "(" */
        try
        {
            acceptTerminal(Token.leftParenthesis);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <procedure statement>. Was expecting a \"(\" after \"identifier\" to start off an <argument list>", cexc);
        }

        /** Try to parse an <argument list> */
        try
        {
            _argumentList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <procedure statement> while parsing an <argument list>", cexc);
        }

        /** Try to recognise ")" */
        try
        {
            acceptTerminal(Token.rightParenthesis);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <procedure statement>. Was expecting \")\" after <argument list> to finish <procedure statement>", cexc);
        }
        
        myGenerate.finishNonterminal("<procedure statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<until statement>)
     * by applying the grammar rule:
     * <until statement> ::= do <statement list> until <condition>
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _untilStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<until statement>");

        /** Try to recognise "do" */
        try
        {
            acceptTerminal(Token.doSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <until statement>. Was expecting \"do\" after <until statement> to start <until statement>", cexc);
        }

        /** Try to parse <statement list> */
        try
        {
            _statementList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <until statement> while parsing <statement list>", cexc);
        }

        /** Try to parse "until" */
        try
        {
            acceptTerminal(Token.untilSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <until statement>. Was expecting \"until\" after <statement list>", cexc);
        }

        /** Try to parse <condition> */
        try
        {
            _condition_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <until statement> while parsing <condition>", cexc);
        }
        
        myGenerate.finishNonterminal("<until statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<for statement>)
     * by applying the grammar rule:
     * <for statement> ::= for ( <assignement statement> ; <condition> ; <assignement statement> ) 
     *                     do <statement list> end loop
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _forStatement_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<for statement>");

        /** Try to recognise "for" */
        try
        {
            acceptTerminal(Token.forSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \"for\" to start <for statement>", cexc);
        }

        /** Try to recognise the "(" */
        try
        {            
            acceptTerminal(Token.leftParenthesis);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \"(\" after \"for\"", cexc);
        }

        /** Try to parse an <assignement statement> */
        try
        {
            _assignementStatement_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement> while parsing <assignement statement>", cexc);
        }

        /** Try to recognise ";" */
        try
        {
            acceptTerminal(Token.semicolonSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \";\" after <assignement statement>", cexc);
        }

        /** Try to parse <condition> */
        try
        {
            _condition_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement> while parsing <condition>", cexc);
        }

        /** Try to recognise ";" */
        try
        {
            acceptTerminal(Token.semicolonSymbol);

        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \";\" after <condition>", cexc);
        }

        /** Try to parse an <assignement statement> */
        try
        {
            _assignementStatement_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement> while parsing <assignement statement>", cexc);
        }

        /** Try to recognise ")" */
        try
        {
            acceptTerminal(Token.rightParenthesis);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \")\" after <assignement statement>", cexc);
        }

        /** Try to recognise "do" */
        try
        {
            acceptTerminal(Token.doSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \"do\" after ( <assignement statement> ; <condition> ; <assignement statement> )", cexc);
        }

        /** Try to parse <statement list> */
        try
        {
            _statementList_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement> while parsing <statement list>", cexc);
        }

        /** Try to recognise "end" */
        try
        {
            acceptTerminal(Token.endSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \"end\" after <statement list>", cexc);
        }

        /** Try to recognise "loop" */
        try
        {
            acceptTerminal(Token.loopSymbol);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <for statement>. Was expecting \"loop\" after \"end\" to finish <for statement>", cexc);
        }

        myGenerate.finishNonterminal("<for statement>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<argument list>)
     * by applying the grammar rule:
     * <argument list> ::= identifier {, identifier}
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _argumentList_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<argument list>");

        /** Try to recognise "identifier" */
        try
        {
            acceptTerminal(Token.identifier);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <argument list>. Was expecting \"identifier\" in the beginning of <argument list>", cexc);
        }

        /** Try to parse  {, identifier}*/
        while(nextToken.symbol == Token.commaSymbol)
        {
            try
            {
                acceptTerminal(Token.commaSymbol);
                acceptTerminal(Token.identifier);
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <argument list>. Was expecting {, identifier}", cexc);
            }
        }
            
        myGenerate.finishNonterminal("<argument list>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<condition>)
     * by applying the grammar rule:
     * <condition> ::= identifier <conditional operator> <new CONDITIONAL REMAINDER>
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _condition_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<condition>");
        
        /** Try to recognise "identifier" */
        try
        {
            acceptTerminal(Token.identifier);
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <condition>. Was expecting \"identifier\" in the beginning of <condition>", cexc);
        }

        /** Try to parse <conditional operator> */
        try
        {
            _conditionalOperator_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <condition>. Was expecting <conditional operator> after \"identifier\"", cexc);
        }

        /** Try to parse <new CONDITIONAL REMAINDER> */
        try
        {
            _newConditionRemainder_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <condition> while parsing <new CONDITIONAL REMAINDER>", cexc);
        }
            
        myGenerate.finishNonterminal("<condition>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<new CONDITIONAL REMAINDER>)
     * by applying the grammar rule:
     * <new CONDITIONAL REMAINDER> ::= identifier | numberConstant | stringConstant
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _newConditionRemainder_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<new CONDITIONAL REMAINDER>");
        
        /**
         * case to recognise every member of First(<new CONDITIONAL REMAINDER>) 
         */
        switch(nextToken.symbol)
        {
            case Token.identifier:
            acceptTerminal(Token.identifier);
            break;

            case Token.numberConstant:
            acceptTerminal(Token.numberConstant);
            break;

            case Token.stringConstant:
            acceptTerminal(Token.stringConstant);
            break;

            default:
            throw new CompilationException(nextToken + " in <new CONDITIONAL REMAINDER>. Was expecting \"identifier\" or \"numberConstant\" or \"stringConstant\"");
        }
        myGenerate.finishNonterminal("<new CONDITIONAL REMAINDER>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<new CONDITIONAL REMAINDER>)
     * by applying the grammar rule:
     * <conditional operator> ::= > | >= | + | /= | < | <=
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _conditionalOperator_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<conditional operator>");

        /**
         * case to recognise every member of First(<conditional operator>) 
         */
        switch(nextToken.symbol)
        {
            /** Try to recognise > */
            case Token.greaterThanSymbol:
            acceptTerminal(Token.greaterThanSymbol);
            break;

            /** Try to recognise >= */
            case Token.greaterEqualSymbol:
            acceptTerminal(Token.greaterEqualSymbol);
            break;

            /** Try to recognise = */
            case Token.equalSymbol:
            acceptTerminal(Token.equalSymbol);
            break;

            /** Try to recognise /= */
            case Token.notEqualSymbol:
            acceptTerminal(Token.notEqualSymbol);
            break;

            /** Try to recognise < */
            case Token.lessThanSymbol:
            acceptTerminal(Token.lessThanSymbol);
            break;

            /** Try to recognise <= */
            case Token.lessEqualSymbol:
            acceptTerminal(Token.lessEqualSymbol);
            break;

            /** if neither... report an error */
            default:
            myGenerate.reportError(nextToken, " in <conditional operator>. Was expecting \">\" or \"=\" or \">=\" or \"+\" or \"/=\" or \"<\" or \"<=\"");
        }

        myGenerate.finishNonterminal("<conditional operator>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<expression>)
     * by applying the grammar rule:
     * <expression> ::= <term> <new EXPRESSION REMAINDER>
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _expression_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<expression>");

        /** Try to parse <term> */
        try
        {
            _term_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <expression> while parsing <term>", cexc);
        }

        /** Try to parse <new EXPRESSION REMAINDER> */
        try
        {
            _newExpressionRemainder_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <expression> while parsing <new EXPRESSION REMAINDER>", cexc);
        }
           
        myGenerate.finishNonterminal("<expression>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<expression>)
     * by applying the grammar rule:
     * <new EXPRESSION REMAINDER> ::= - <term> <new EXPRESSION REMAINDER> | + <term> <new EXPRESSION REMAINDER> | ε
     * Throws a Compilation Exception if the non-terminal is not recognised
     * 
     **/
    private void _newExpressionRemainder_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<new EXPRESSION REMAINDER>");

        /**
         * case to recognise every member of First(<new EXPRESSION REMAINDER>) 
         */
        switch(nextToken.symbol)
        {
            /** Try to parse  - <term> <new EXPRESSION REMAINDER>*/
            case Token.minusSymbol:
            acceptTerminal(Token.minusSymbol);
            try
            {
                _term_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new EXPRESSION REMAINDER> while parsing <term> after \"-\"", cexc);
            }
            try
            {
                _newExpressionRemainder_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new EXPRESSION REMAINDER> while parsing  <new EXPRESSION REMAINDER>", cexc);
            }
            break;

            /** Try to parse  + <term> <new EXPRESSION REMAINDER>*/
            case Token.plusSymbol:
            acceptTerminal(Token.plusSymbol);
            try
            {
                _term_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new EXPRESSION REMAINDER> while parsing <term> after \"+\"", cexc);
            }
            try
            {
                _newExpressionRemainder_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new EXPRESSION REMAINDER> while parsing  <new EXPRESSION REMAINDER>", cexc);
            }
            break;
            
            /** To recognise  ε, have to consider Follow(<new EXPRESSION REMAINDER>)*/
            case Token.semicolonSymbol:
            break;
            case Token.endSymbol:
            break;
            case Token.rightParenthesis:
            break; 

            /** if neither... Report an error */
            default:
            myGenerate.reportError(nextToken, " in <new EXPRESSION REMAINDER>. Was expecting \"-\" or \"+\" or \";\" or \"end\" or \")\"");
        }

        myGenerate.finishNonterminal("<new EXPRESSION REMAINDER>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<term>)
     * by applying the grammar rule:
     * <term> ::= <factor> <new TERM REMAINDER>
     * Reports error if the non-terminal is not recognised
     * 
     **/
    private void _term_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<term>");

        /** Try to parse <factor> */
        try
        {
            _factor_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <term> while parsing <factor>", cexc);
        }

        /** Try to parse <new TERM REMAINDER> */
        try
        {
            _newTermRemainder_();
        }
        catch (CompilationException cexc)
        {
            throw new CompilationException(nextToken + " in <term> while parsing <new TERM REMAINDER>", cexc);
        }

        myGenerate.finishNonterminal("<term>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<new TERM REMAINDER>)
     * by applying the grammar rule:
     * <new TERM REMAINDER> ::= * <factor> <new TERM REMAINDER> | / <factor> <new TERM REMAINDER> | ε
     * Reports error if the non-terminal is not recognised
     * 
     **/
    private void _newTermRemainder_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<new TERM REMAINDER>");

        /**
         * case to recognise every member of First(<new TERM REMAINDER>) 
         */
        switch(nextToken.symbol)
        {
            /** Try to recognise  "* <factor> <new TERM REMAINDER>" */
            case Token.timesSymbol:
            acceptTerminal(Token.timesSymbol); 
            try
            {
                _factor_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new TERM REMAINDER> while parsing <factor>", cexc);
            }
            try
            {
                _newTermRemainder_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new TERM REMAINDER> while parsing <new TERM REMAINDER>", cexc);
            }
            break;

            /** Try to recognise "/ <factor> <new TERM REMAINDER>" */
            case Token.divideSymbol:
            acceptTerminal(Token.divideSymbol);
            try
            {
                _factor_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new TERM REMAINDER> while parsing <factor>", cexc);
            }
            try
            {
                _newTermRemainder_();
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <new TERM REMAINDER> while parsing <new TERM REMAINDER>", cexc);
            }
            break;

            /**
            * case to recognise every member of Follow(<factor>) 
            * because of NULL production
            */
            case Token.semicolonSymbol:
            break;
            case Token.endSymbol:
            break;
            case Token.rightParenthesis:
            break;
            case Token.minusSymbol:
            break;
            case Token.plusSymbol:
            break;

            /** If neither... Report an error */
            default:
            myGenerate.reportError(nextToken, " in <new TERM REMAINDER>. Was expecting \"*\", \"/\", \";\", \"end\" or \")\" or \"+\" or \"-\"");
        }
        myGenerate.finishNonterminal("<new TERM REMAINDER>");
    }

    /** 
     * 
     * This method recognises the non-terminal (<factor>)
     * by applying the grammar rule:
     * <factor> ::= identifier | numberConstant | ( <expression> )
     * Reports error if the non-terminal is not recognised
     * 
     **/
    private void _factor_() throws IOException, CompilationException
    {
        myGenerate.commenceNonterminal("<factor>");
        /**
         * case to recognise every member of First(<factor>) 
         */
        switch(nextToken.symbol)
        {
            /** Try to recognise  "identifier" */
            case Token.identifier:
            acceptTerminal(Token.identifier);
            break;

            /** Try to recognise  "numberConstant" */
            case Token.numberConstant:
            acceptTerminal(Token.numberConstant); 
            break;

            /** Try to recognise  "( <expression> )" */
            case Token.leftParenthesis:
            try
            {
                acceptTerminal(Token.leftParenthesis);
                _expression_();
                acceptTerminal(Token.rightParenthesis);
            }
            catch (CompilationException cexc)
            {
                throw new CompilationException(nextToken + " in <factor> while parsing \"( <expression> )\"", cexc);
            }
            break;

            default:
            myGenerate.reportError(nextToken, " in <factor>.  Was expecting \"identifier\" or \"numberConstant\" or \"(\"");
        }
        
        myGenerate.finishNonterminal("<factor>");
    }
}
