@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!perl
#line 15
use DirHandle;
@java = ();

@dirs = ("src/com/hp/hpl/guess");

while ($#dirs >= 0) {
    $dr = shift(@dirs);
    $d = new DirHandle $dr;
    if (defined $d) {
	while (defined($_ = $d->read)) { 
	    if (-d "$dr/$_") {
		if ($_ !~ /\./) {
		    @dirs = (@dirs,"$dr/$_");
		}
	    } elsif ($_ =~ /.java$/gio) {
		@java = (@java,"$dr/$_");
	    }
	}
	undef $d;
    }
}

open(OUTFILE,">scripts/Main.py");
open(OUTFILE2,">scripts/Main-applet.py");

print OUTFILE "\n# AUTOMATICALLY GENERATED\n";
print OUTFILE "# Defines wrapper methods that can be used as shortcuts in the interpreter\n\n";

print OUTFILE2 "\n# AUTOMATICALLY GENERATED\n";
print OUTFILE2 "# Defines wrapper methods that can be used as shortcuts in the interpreter\n\n";

%argcounts = {};
%argsig = {};
%argdefs = {};
%voids = {};
%docs = {};

sub processdoc() {
    my @d = split(/\n/,$_[0]);
    my $dfunc = $_[1];
    my $sg = $_[2];
    #print "$sg\n";
    my $rdoc = "$sg\n";
    foreach $d (@d) {
	next if ($d =~ /\@pyexport/);
	$d =~ s/\s*\*//;
	$d =~ s/\@/\t/;
	$rdoc .= "$d\n";
	#print "$d\n";
    }
    if (defined($docs{$dfunc})) {
	$docs{$dfunc} .= "\n$rdoc";
    } else {
	$docs{$dfunc} = "$rdoc";
    }
}

foreach $java (@java) {
    open(INFILE,$java);
    $pyobj = "";
    $doc = "";
    $indoc = 0;
    while(<INFILE>) {
	$_ =~ s/\cM//gio;
	if ($_ =~ /^\s*\/\*\*/) {
	    $indoc = 1;
	} elsif ($_ =~ /^\s*\*\//) {
	    $indoc = 0;
	    if ($doc !~ /\@pyexport/) {
		$doc = "";
	    }
	    #$doc = "";
	}

	if (($indoc) && ($_ =~ /^\s*\*/)){
	    $doc .= $_;
	}

	chop($_);
	    
	if ($_ =~ /\@pyobj\s+(.*)/) {
	    $pyobj = $1;
	    next;
	}
	if ($_ =~ /\@pyimport\s(.*)/) {
	    print OUTFILE $1."\n\n";
	    next;
	}
	if ($_ =~ /\@pyexport\s*(.*)/) {
	    $pyfun = $1;
	    $func = "";
	    while(<INFILE>) {
		chop($_);
		$func .= $_;
		last if (($_ =~ /\{/) || ($_ =~ /\;/));
	    }
	    ($func) = $func =~ /(public.*)/i;
	    $func =~ s/\s+/ /gio;
	    ($funcname) = $func =~ /(\S+)\s*\(/;
	    ($args) = $func =~ /\(([^\)]+)/;
	    @args = split(/\s*\,\s*/,$args);
	    $args = "";
	    $argc = $#argc;
	    foreach $arg (@args) {
		($type,$name) = $arg =~ /(\S+)\s+(\S+)/;
		$args .= "$name,";
	    }
	    chop($args);

	    $void = "";

	    ($first,@rest) = split(/\(/,$func);
	    $sig = "$first(@rest";
	    $sig =~ s/\{\s*//gio;
	    $sig =~ s/\s*\}//gio;
	    $sig =~ s/public //gio;
	    $sig =~ s/private //gio;
	    $sig =~ s/protected //gio;
	    #print "$sig\n";
	    if ($first =~ /\s+void\s+/gio) {
		$void = "";
	    } else {
		$void = "return ";
	    }

	    #print "$void\n";

	    if ($pyfun ne "") {
		if ($pyfun =~ /\(/) {
		    # total override
		    print OUTFILE "def $pyfun:\n";
		    print OUTFILE "\t$void$pyobj.$funcname($args)\n\n";
		    print OUTFILE2 "def $pyfun:\n";
		    print OUTFILE2 "\t$void$pyobj.$funcname($args)\n\n";
		    next;
		} else {
		    #print OUTFILE "def $pyfun($args):\n";
		}
	    } else {
		$pyfun = $funcname;
		#print OUTFILE "def $funcname($args):\n";
	    }

	    #print "$pyfun($args)\n";

	    $voids{$pyfun} .= "|$void";
	    $argcounts{$pyfun} .= "|$#args";
	    $argsig{$pyfun} .= "|$args";
	    $argdefs{$pyfun} .= "|$pyobj.$funcname";

	    &processdoc($doc,$pyfun,$sig);
	    $doc = "";
	}	
    }
    #print "$java $pyobj\n" if ($pyobj ne "");
}

foreach $pyfun (keys %argcounts) {
    next if ($pyfun =~ /HASH/);
    #$print "$pyfun\n";

    @argcs = split(/\|/,$argcounts{$pyfun});
    shift(@argcs);
    #print "$pyfun @argcs\n";

    @argsig = split(/\|/,$argsig{$pyfun});
    shift(@argsig);

    @argdefs = split(/\|/,$argdefs{$pyfun});
    shift(@argdefs);

    @voids = split(/\|/,$voids{$pyfun});
    shift(@voids);

    #print "@voids\n";

    if ($#argcs == 0) {
	print OUTFILE "def $pyfun($argsig[0]):\n";
	print OUTFILE "\t$voids[0]$argdefs[0]($argsig[0])\n\n";
	print OUTFILE2 "def $pyfun($argsig[0]):\n";
	print OUTFILE2 "\t$voids[0]$argdefs[0]($argsig[0])\n\n";
    } else {
	print OUTFILE "def $pyfun(*d):\n";
	print OUTFILE2 "def $pyfun(*d):\n";
	$ifit = "if";
	for ($i = 0 ; $i <= $#argcs ; $i++) {
	    $req = $argcs[$i];
	    $v = $voids[$i];
	    #print "$v\n";
	    $req++;
	    print OUTFILE "\t$ifit len(d) == $req:\n";
	    print OUTFILE "\t\t$v$argdefs[$i](";
	    print OUTFILE2 "\t$ifit len(d) == $req:\n";
	    print OUTFILE2 "\t\t$v$argdefs[$i](";
	    for ($d = 0 ; $d < $req ; $d++) {
		if ($d > 0) {
		    print OUTFILE ",";
		    print OUTFILE2 ",";
		}
		print OUTFILE "d[$d]";
		print OUTFILE2 "d[$d]";
	    }
	    print OUTFILE ")\n";
	    print OUTFILE2 ")\n";
	    $ifit = "elif";
	}
	print OUTFILE "\telse:\n";
	print OUTFILE "\t\traise ValueError(\"Incorrect number of arguments\")\n";
	print OUTFILE "\n\n";
	print OUTFILE2 "\telse:\n";
	print OUTFILE2 "\t\traise ValueError(\"Incorrect number of arguments\")\n";
	print OUTFILE2 "\n\n";
    }
}

$dict = "";

foreach $key (keys %docs) {
    next if ($key =~ /HASH/);
    #print "$key\n$docs{$key}\n";
    $f = $docs{$key};
    $f =~ s/\n/\\n/gio;
    $f =~ s/\t/\\t/gio;
    $f =~ s/\"/\'/gio;
    if ($dict eq "") {
	$dict = "\"$key\" : \"$f\"";
    } else {
	$dict .= ", \"$key\" : \"$f\"";
    }
}

#print "$dict\n";

print OUTFILE "\n\n__FUNCTION_DICTIONARY = {$dict}\n\n";
print OUTFILE2 "\n\n__FUNCTION_DICTIONARY = {$dict}\n\n";

close(OUTFILE);
close(OUTFILE2);

system("cat scripts/MainPre.py >> scripts/Main.py");
system("cat scripts/MainPre.py >> scripts/Main-applet.py");
system("cp scripts/MainPre.py scripts/Functions.py");

__END__
:endofperl
