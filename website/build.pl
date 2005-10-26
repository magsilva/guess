@files = split(/\n/,`ls *.tmpl`);
foreach $file (@files) {
    $fileout = $file;
    $fileout =~ s/tmpl/html/;
    system("cat $file sidebars.txt > $fileout");
}
