from play.utils import *
from optparse import OptionParser
import yaml
import shutil
import os

COMMANDS = ['repository-commit', 'rc']

HELP = {
    'repository-commit': 'publish module in repository',
    'rc': 'alias repository-commit'
}

def execute(**kargs):
    app = kargs.get("app")
    args = kargs.get("args")
    play_env = kargs.get("env")

    parser = OptionParser()
    parser.add_option("-r", "--repository", dest="repository",help="repository dir path")
    parser.add_option("-s", "--suffix", dest="suffix",help="repository dir path")
    parser.add_option("-a", action="store_true", dest="all")

    (options, args) = parser.parse_args()
    
    print '~ '
    print '~ Commiting to repository server'
    print '~ '

    app.check()

    repository = options.repository 
    suffix = options.suffix 

    if repository:
        print '~ Found repository repository : %s' % repository
    else:
        print '~ No repository server configured'
        print '~ You must define path repository (-r path/to/repository)'
        print '~ '
        sys.exit(0)

    from_dir_deps = os.path.join(app.path, 'dist')
    deps_file = 'dependencies.yml'
    app_name, module_version = extract_name_version(app, os.path.join(from_dir_deps, deps_file))

    from_dir_dist = os.path.join(app.path, 'dist')

    #Only interested on .zip files
    zip_files = []
    for root, dirs, files in os.walk(from_dir_dist):
        zip_files += [ fi for fi in files if fi.endswith(".zip") ]

    #Loop over all found files
    if len(zip_files) >0:
        for file in zip_files:

            expected_file = "%s-%s.zip" % (app_name, module_version)
	    print "Expected %s found %s" % (expected_file, file)

            if expected_file == file:
                if options.all:
                    #We won't ask the user if he wants to commit
                    resp = "Y"
                else:
                    resp = raw_input('~ Do you want to post %s to repository? (Y/N) ' % file)
                if resp == 'Y':
                    to_dir = os.path.join(repository, app_name, module_version)
                    if not os.path.exists(to_dir):
                        os.makedirs(to_dir)
                    copy_file_to_dir(file, from_dir_dist, to_dir)
                    copy_file_to_dir(deps_file, from_dir_deps, to_dir)
                else:
                    print '~ '
                    print '~ Skiping %s' % file
            else:
                print '~ '
                print '~ Skiping %s' % file
    else:
        print '~ '
        print '~ No module build found.'
        print '~ Try "play build-module" command first'
        print '~ '

def extract_name_version(app, deps_file):
    #Getting module version from dependencies file
    if os.path.exists(deps_file):
        f = open(deps_file)
        deps = yaml.load(f.read())
        #Is this a Play~ module?
        if "self" in deps:
            d = deps["self"].split(" ")
            module_version = d.pop()
            app_name = d.pop()
        else:
            app_name = app.name()
            print '~ This is not a Play module'
            module_version = app.readConf('application.version')
            if not module_version:
                print '~ '
                print '~ No application.version found in application.conf file'
                print '~ '
                module_version = raw_input('~ Provide version number to be pushed to Repository:')
        f.close

    if module_version:
        print '~ Module version : %s' % module_version
        print '~ '
    else:
        print '~ No module version configured.'
        print '~ Configure your dependencies file properly'
        sys.exit(1)
    return app_name, module_version

def copy_file_to_dir(file, from_dir, to_dir):
    print '~ '
    print '~ Copying %s to %s' % (file, to_dir)

    from_path = os.path.join(from_dir, file)
    to_path = os.path.join(to_dir, file)
    try:
        shutil.copy2(from_path, to_path)
    except:
        print '~ Error: could not copy file %s to %s' % (from_path, to_path)
        raise 
