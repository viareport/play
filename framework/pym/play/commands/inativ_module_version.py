from play.utils import *
from optparse import OptionParser
import yaml
import shutil
import os

COMMANDS = ['module-version']

HELP = {
    'module-version': 'print module version',
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
    
    app.check()

    from_dir_deps = os.path.join(app.path, 'conf')
    deps_file = 'dependencies.yml'
    _, version = extract_name_version(app, os.path.join(from_dir_deps, deps_file))
    print version

def extract_name_version(app, deps_file):
    #Getting module version from dependencies file

    module_version = None

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
