package org.seasar.cadhelin.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validator;

public class EmailValidator extends AbstractValidator {
	private String errorMessageKey;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	
	public EmailValidator() {
	}
	public EmailValidator(String errorMessageKey,String[] arguments){
		this.errorMessageKey = (errorMessageKey!=null)? 
				errorMessageKey :
				"error.string.email" ;
	}
	public Validator createValidater(Validate validate) {
		return new EmailValidator(errorMessageKey,validate.args());
	}
	public String getValidaterKey() {
		return "email";
	}

	public boolean validate(String name, Object value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(!isValid(string)){
				errors.put(name,
						new Message(errorMessageKey + "." + name,
								messageArguments));				
				return true;
			}
		}
		return false;
	}
    private static final String SPECIAL_CHARS = "\\(\\)<>@,;:'\\\\\\\"\\.\\[\\]";
    private static final String VALID_CHARS = "[^\\s" + SPECIAL_CHARS + "]";
    private static final String QUOTED_USER = "(\"[^\"]*\")";
    private static final String ATOM = VALID_CHARS + '+';
    private static final String WORD = "((" + VALID_CHARS + "|')+|" + QUOTED_USER + ")";

    // Each pattern must be surrounded by /
    private static final String LEGAL_ASCII_PATTERN = "^[\\00-\\0177]+$";
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";
    private static final String IP_DOMAIN_PATTERN =
            "^\\[(\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})[.](\\d{1,3})\\]$";
    private static final String TLD_PATTERN = "^([a-zA-Z]+)$";
            
    private static final String USER_PATTERN = "^\\s*" + WORD + "(\\." + WORD + ")*$";
    private static final String DOMAIN_PATTERN = "^" + ATOM + "(\\." + ATOM + ")*\\s*$";
    private static final String ATOM_PATTERN = "(" + ATOM + ")";

    Pattern matchAsciiPat = Pattern.compile(LEGAL_ASCII_PATTERN);
    Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * <p>Checks if a field has a valid e-mail address.</p>
     *
     * @param email The value validation is being performed on.  A <code>null</code>
     * value is considered invalid.
     * @return true if the email address is valid.
     */
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }

        if (!matchAsciiPat.matcher(email).matches()) {
            return false;
        }

        email = stripComments(email);

        // Check the whole email address structure
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            return false;
        }

        if (email.endsWith(".")) {
            return false;
        }

        if (!isValidUser(emailMatcher.group(1))) {
            return false;
        }

        if (!isValidDomain(emailMatcher.group(2))) {
            return false;
        }

        return true;
    }
    Pattern ipAddressPattern = Pattern.compile(IP_DOMAIN_PATTERN);
    Pattern domainPattern = Pattern.compile(DOMAIN_PATTERN);
    /**
     * Returns true if the domain component of an email address is valid.
     * @param domain being validatied.
     * @return true if the email address's domain is valid.
     */
    protected boolean isValidDomain(String domain) {
        boolean symbolic = false;
        Matcher ipAddressMatcher = ipAddressPattern.matcher(domain);
        if (ipAddressMatcher.matches()) {
            if (!isValidIpAddress(ipAddressMatcher)) {
                return false;
            } else {
                return true;
            }
        } else {
            // Domain is symbolic name
            symbolic = domainPattern.matcher(domain).matches();
        }

        if (symbolic) {
            if (!isValidSymbolicDomain(domain)) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
    Pattern userPattern = Pattern.compile(USER_PATTERN);
    /**
     * Returns true if the user component of an email address is valid.
     * @param user being validated
     * @return true if the user name is valid.
     */
    protected boolean isValidUser(String user) {
        return userPattern.matcher(user).matches();
    }

    /**
     * Validates an IP address. Returns true if valid.
     * @param ipAddressMatcher Pattren matcher
     * @return true if the ip address is valid.
     */
    protected boolean isValidIpAddress(Matcher ipAddressMatcher) {
        for (int i = 1; i <= 4; i++) {
            String ipSegment = ipAddressMatcher.group(i);
            if (ipSegment == null || ipSegment.length() <= 0) {
                return false;
            }

            int iIpSegment = 0;

            try {
                iIpSegment = Integer.parseInt(ipSegment);
            } catch(NumberFormatException e) {
                return false;
            }

            if (iIpSegment > 255) {
                return false;
            }

        }
        return true;
    }
    Pattern atomPattern = Pattern.compile(ATOM_PATTERN);
    Pattern tldPat = Pattern.compile(TLD_PATTERN);
    /**
     * Validates a symbolic domain name.  Returns true if it's valid.
     * @param domain symbolic domain name
     * @return true if the symbolic domain name is valid.
     */
    protected boolean isValidSymbolicDomain(String domain) {
        String[] domainSegment = new String[10];
        boolean match = true;
        int i = 0;
        while (match) {
            Matcher atomMatcher = atomPattern.matcher(domain);
			match = atomMatcher.lookingAt();
            if (match) {
                domainSegment[i] = atomMatcher.group(1);
                int l = domainSegment[i].length() + 1;
                domain =
                        (l >= domain.length())
                        ? ""
                        : domain.substring(l);

                i++;
            } 
        }

        int len = i;
        
        // Make sure there's a host name preceding the domain.
        if (len < 2) {
            return false;
        }
        
        // TODO: the tld should be checked against some sort of configurable 
        // list
        String tld = domainSegment[len - 1];
        if (tld.length() > 1) {
            if (!tldPat.matcher(tld).matches()) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }
    /**
     *   Recursively remove comments, and replace with a single space.  The simpler
     *   regexps in the Email Addressing FAQ are imperfect - they will miss escaped
     *   chars in atoms, for example.
     *   Derived From    Mail::RFC822::Address
     * @param emailStr The email address
     * @return address with comments removed.
    */
    protected String stripComments(String emailStr)  {
//     String input = emailStr;
//     String result = emailStr;
//     String commentPat = "s/^((?:[^\"\\\\]|\\\\.)*(?:\"(?:[^\"\\\\]|\\\\.)*\"(?:[^\"\\\\]|\111111\\\\.)*)*)\\((?:[^()\\\\]|\\\\.)*\\)/$1 /osx";
//     result = commentMatcher.substitute(commentPat,input);
//     // This really needs to be =~ or Perl5Matcher comparison
//     while (!result.equals(input)) {
//        input = result;
//        result = commentMatcher.substitute(commentPat,input);
//     }
//     return result;
    	return emailStr;
    }

}
