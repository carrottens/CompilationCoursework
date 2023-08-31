
/** 
 *
 * SCC312
 * Coursework 2
 * Compilation
 * 
 * Generate extends AbstractGenerate.  
 * Accepts or reports an error in syntax from the rest of the system.
 *
 * @author: Viltene
 *
 **/

public class Generate extends AbstractGenerate{

    /** Report an error to the user. */

    @Override 
    public void reportError( Token token, String explanatoryMessage )  throws CompilationException
    {
        CompilationException ce = new CompilationException(token + explanatoryMessage);
        System.out.println( token + explanatoryMessage);
        System.out.println( "312FAIL" );
        throw ce;
    }
    
}
